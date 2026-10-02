package com.example.dreambyte.data

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import com.example.dreambyte.model.AppInfo
import com.example.dreambyte.model.DesktopItem
import com.example.dreambyte.model.DesktopItemType
import com.example.dreambyte.model.DeviceMetrics
import com.example.dreambyte.model.DeviceType
import com.example.dreambyte.model.DrawerLayoutMode
import com.example.dreambyte.model.DreamByteAppearance
import com.example.dreambyte.model.FolderInfo
import com.example.dreambyte.model.IconSize
import com.example.dreambyte.model.LauncherSettings
import com.example.dreambyte.model.WallpaperType
import com.example.dreambyte.model.WidgetType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class LauncherRepository(private val context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("dreambyte_launcher_prefs", Context.MODE_PRIVATE)

  private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
  val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

  private val _desktopItems = MutableStateFlow<List<DesktopItem>>(emptyList())
  val desktopItems: StateFlow<List<DesktopItem>> = _desktopItems.asStateFlow()

  private val _dockApps = MutableStateFlow<List<String>>(emptyList())
  val dockApps: StateFlow<List<String>> = _dockApps.asStateFlow()

  private val _folders = MutableStateFlow<List<FolderInfo>>(emptyList())
  val folders: StateFlow<List<FolderInfo>> = _folders.asStateFlow()

  private val _settings = MutableStateFlow(loadSettings())
  val settings: StateFlow<LauncherSettings> = _settings.asStateFlow()

  private val _deviceMetrics = MutableStateFlow(readMetrics())
  val deviceMetrics: StateFlow<DeviceMetrics> = _deviceMetrics.asStateFlow()

  private val batteryReceiver = object : BroadcastReceiver() {
    override fun onReceive(c: Context?, intent: Intent?) {
      updateBatteryMetrics(intent)
    }
  }

  private val packageReceiver = object : BroadcastReceiver() {
    override fun onReceive(c: Context?, intent: Intent?) {
      // Reload installed apps whenever a package is added, removed or changed
      loadInstalledApps()
    }
  }

  init {
    loadDesktopAndDock()
    registerReceivers()
    loadInstalledApps()
  }

  private fun registerReceivers() {
    try {
      val batteryFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
      val batteryStatusIntent = context.registerReceiver(batteryReceiver, batteryFilter)
      updateBatteryMetrics(batteryStatusIntent)
    } catch (_: Exception) {}

    try {
      val packageFilter = IntentFilter().apply {
        addAction(Intent.ACTION_PACKAGE_ADDED)
        addAction(Intent.ACTION_PACKAGE_REMOVED)
        addAction(Intent.ACTION_PACKAGE_REPLACED)
        addDataScheme("package")
      }
      context.registerReceiver(packageReceiver, packageFilter)
    } catch (_: Exception) {}
  }

  fun unregisterReceivers() {
    try {
      context.unregisterReceiver(batteryReceiver)
    } catch (_: Exception) {}
    try {
      context.unregisterReceiver(packageReceiver)
    } catch (_: Exception) {}
  }

  fun loadInstalledApps() {
    val pm = context.packageManager
    val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
      addCategory(Intent.CATEGORY_LAUNCHER)
    }

    val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      pm.queryIntentActivities(mainIntent, PackageManager.ResolveInfoFlags.of(0L))
    } else {
      @Suppress("DEPRECATION")
      pm.queryIntentActivities(mainIntent, 0)
    }

    val appsList = resolveInfos.mapNotNull { resolveInfo ->
      try {
        val packageName = resolveInfo.activityInfo.packageName
        // Do not include DreamByte Launcher itself in the apps list if we want to avoid self-launch loop
        val activityName = resolveInfo.activityInfo.name
        val label = resolveInfo.loadLabel(pm).toString().ifBlank { packageName }
        val iconDrawable = resolveInfo.loadIcon(pm)
        val iconBitmap = drawableToBitmap(iconDrawable)
        val isSystem = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

        AppInfo(
          packageName = packageName,
          activityName = activityName,
          label = label,
          iconBitmap = iconBitmap,
          isSystemApp = isSystem
        )
      } catch (_: Exception) {
        null
      }
    }.distinctBy { it.packageName }.sortedBy { it.label.lowercase() }

    _installedApps.value = appsList

    // If first run, initialize default desktop and dock
    if (_desktopItems.value.isEmpty()) {
      seedDefaultDesktop(appsList)
    }
  }

  private fun updateBatteryMetrics(batteryIntent: Intent?) {
    if (batteryIntent == null) return
    val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
    val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
    val pct = if (level >= 0 && scale > 0) (level * 100) / scale else 100

    val status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
    val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
      status == BatteryManager.BATTERY_STATUS_FULL

    val statusString = when (status) {
      BatteryManager.BATTERY_STATUS_CHARGING -> "Cargando"
      BatteryManager.BATTERY_STATUS_FULL -> "Carga Completa"
      BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Conectado, sin cargar"
      else -> "En batería"
    }

    _deviceMetrics.value = _deviceMetrics.value.copy(
      batteryLevel = pct,
      isCharging = isCharging,
      batteryStatus = statusString
    )
  }

  fun refreshHardwareMetrics() {
    _deviceMetrics.value = readMetrics().copy(
      batteryLevel = _deviceMetrics.value.batteryLevel,
      isCharging = _deviceMetrics.value.isCharging,
      batteryStatus = _deviceMetrics.value.batteryStatus
    )
  }

  private fun readMetrics(): DeviceMetrics {
    var totalRam = 4096L
    var availRam = 2048L
    try {
      val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
      val memInfo = ActivityManager.MemoryInfo()
      actManager?.getMemoryInfo(memInfo)
      totalRam = memInfo.totalMem / (1024 * 1024)
      availRam = memInfo.availMem / (1024 * 1024)
    } catch (_: Exception) {}

    var totalStorage = 64L
    var availStorage = 32L
    try {
      val stat = StatFs(Environment.getDataDirectory().path)
      val bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong
      val bytesTotal = stat.blockSizeLong * stat.blockCountLong
      totalStorage = bytesTotal / (1024 * 1024 * 1024)
      availStorage = bytesAvailable / (1024 * 1024 * 1024)
    } catch (_: Exception) {}

    return DeviceMetrics(
      totalRamMb = totalRam,
      availableRamMb = availRam,
      totalStorageGb = totalStorage,
      availableStorageGb = availStorage,
      deviceModel = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}",
      androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    )
  }

  private fun seedDefaultDesktop(apps: List<AppInfo>) {
    val items = mutableListOf<DesktopItem>()

    // Page 0: Widgets at top
    items.add(
      DesktopItem(
        id = UUID.randomUUID().toString(),
        type = DesktopItemType.WIDGET,
        page = 0,
        cellX = 0,
        cellY = 0,
        spanX = 4,
        spanY = 2,
        widgetType = WidgetType.CLOCK
      )
    )

    items.add(
      DesktopItem(
        id = UUID.randomUUID().toString(),
        type = DesktopItemType.WIDGET,
        page = 0,
        cellX = 0,
        cellY = 2,
        spanX = 4,
        spanY = 1,
        widgetType = WidgetType.DREAMBOT
      )
    )

    // A few staple apps on Page 0
    var currY = 3
    var currX = 0
    val maxCols = _settings.value.gridCols
    for (app in apps.take(8)) {
      if (currY < _settings.value.gridRows) {
        items.add(
          DesktopItem(
            id = UUID.randomUUID().toString(),
            type = DesktopItemType.APP,
            page = 0,
            cellX = currX,
            cellY = currY,
            packageName = app.packageName,
            activityName = app.activityName,
            label = app.label
          )
        )
        currX++
        if (currX >= maxCols) {
          currX = 0
          currY++
        }
      }
    }

    // Page 1: System info & Battery widgets + more apps
    items.add(
      DesktopItem(
        id = UUID.randomUUID().toString(),
        type = DesktopItemType.WIDGET,
        page = 1,
        cellX = 0,
        cellY = 0,
        spanX = 4,
        spanY = 1,
        widgetType = WidgetType.BATTERY
      )
    )

    items.add(
      DesktopItem(
        id = UUID.randomUUID().toString(),
        type = DesktopItemType.WIDGET,
        page = 1,
        cellX = 0,
        cellY = 1,
        spanX = 4,
        spanY = 2,
        widgetType = WidgetType.DEVICE_INFO
      )
    )

    _desktopItems.value = items
    saveDesktopItems(items)

    // Seed dock: pick phone/contacts, messages, browser, camera, settings if available
    val dockPkgs = mutableListOf<String>()
    val candidatePrefs = listOf("dialer", "phone", "contact", "message", "sms", "chrome", "browser", "camera", "settings")
    for (cand in candidatePrefs) {
      val found = apps.find { it.packageName.lowercase().contains(cand) || it.label.lowercase().contains(cand) }
      if (found != null && !dockPkgs.contains(found.packageName)) {
        dockPkgs.add(found.packageName)
      }
      if (dockPkgs.size >= 4) break
    }
    // If not enough, pad with first available apps
    for (app in apps) {
      if (dockPkgs.size >= 4) break
      if (!dockPkgs.contains(app.packageName)) {
        dockPkgs.add(app.packageName)
      }
    }

    _dockApps.value = dockPkgs
    saveDockApps(dockPkgs)
  }

  fun saveSettings(newSettings: LauncherSettings) {
    _settings.value = newSettings
    prefs.edit().apply {
      putString("appearance", newSettings.appearance.name)
      putString("wallpaperType", newSettings.wallpaperType.name)
      putFloat("wallpaperBlur", newSettings.wallpaperBlur)
      putInt("gridRows", newSettings.gridRows)
      putInt("gridCols", newSettings.gridCols)
      putString("iconSize", newSettings.iconSize.name)
      putBoolean("animationsEnabled", newSettings.animationsEnabled)
      putBoolean("showDreamBot", newSettings.showDreamBot)
      putString("deviceModeOverride", newSettings.deviceModeOverride.name)
      putString("drawerLayout", newSettings.drawerLayout.name)
      putInt("dockItemCount", newSettings.dockItemCount)
      putString("customWallpaperPath", newSettings.customWallpaperPath)
      putBoolean("interactiveParticlesEnabled", newSettings.interactiveParticlesEnabled)
      putBoolean("firstLaunchCompleted", newSettings.firstLaunchCompleted)
      apply()
    }
  }

  private fun loadSettings(): LauncherSettings {
    return LauncherSettings(
      appearance = try {
        DreamByteAppearance.valueOf(prefs.getString("appearance", DreamByteAppearance.MODERN.name)!!)
      } catch (_: Exception) { DreamByteAppearance.MODERN },
      wallpaperType = try {
        WallpaperType.valueOf(prefs.getString("wallpaperType", WallpaperType.MODERN_HOLO.name)!!)
      } catch (_: Exception) { WallpaperType.MODERN_HOLO },
      wallpaperBlur = prefs.getFloat("wallpaperBlur", 0f),
      gridRows = prefs.getInt("gridRows", 5),
      gridCols = prefs.getInt("gridCols", 4),
      iconSize = try {
        IconSize.valueOf(prefs.getString("iconSize", IconSize.NORMAL.name)!!)
      } catch (_: Exception) { IconSize.NORMAL },
      animationsEnabled = prefs.getBoolean("animationsEnabled", true),
      showDreamBot = prefs.getBoolean("showDreamBot", true),
      deviceModeOverride = try {
        DeviceType.valueOf(prefs.getString("deviceModeOverride", DeviceType.AUTO.name)!!)
      } catch (_: Exception) { DeviceType.AUTO },
      drawerLayout = try {
        DrawerLayoutMode.valueOf(prefs.getString("drawerLayout", DrawerLayoutMode.GRID.name)!!)
      } catch (_: Exception) { DrawerLayoutMode.GRID },
      dockItemCount = prefs.getInt("dockItemCount", 5),
      customWallpaperPath = prefs.getString("customWallpaperPath", null),
      interactiveParticlesEnabled = prefs.getBoolean("interactiveParticlesEnabled", true),
      firstLaunchCompleted = prefs.getBoolean("firstLaunchCompleted", false)
    )
  }

  fun addDesktopItem(item: DesktopItem) {
    val updated = _desktopItems.value.toMutableList()
    updated.add(item)
    _desktopItems.value = updated
    saveDesktopItems(updated)
  }

  fun removeDesktopItem(itemId: String) {
    val updated = _desktopItems.value.filterNot { it.id == itemId }
    _desktopItems.value = updated
    saveDesktopItems(updated)
  }

  fun moveDesktopItem(itemId: String, newPage: Int, newCellX: Int, newCellY: Int) {
    val items = _desktopItems.value.toMutableList()
    val index = items.indexOfFirst { it.id == itemId }
    if (index != -1) {
      items[index] = items[index].copy(page = newPage, cellX = newCellX, cellY = newCellY)
      _desktopItems.value = items
      saveDesktopItems(items)
    }
  }

  fun swapDesktopItems(idA: String, idB: String) {
    val items = _desktopItems.value.toMutableList()
    val indexA = items.indexOfFirst { it.id == idA }
    val indexB = items.indexOfFirst { it.id == idB }
    if (indexA != -1 && indexB != -1) {
      val itemA = items[indexA]
      val itemB = items[indexB]
      items[indexA] = itemA.copy(page = itemB.page, cellX = itemB.cellX, cellY = itemB.cellY)
      items[indexB] = itemB.copy(page = itemA.page, cellX = itemA.cellX, cellY = itemA.cellY)
      _desktopItems.value = items
      saveDesktopItems(items)
    }
  }

  fun moveItemToPage(itemId: String, targetPage: Int) {
    val items = _desktopItems.value.toMutableList()
    val index = items.indexOfFirst { it.id == itemId }
    if (index != -1) {
      val item = items[index]
      if (item.page != targetPage) {
        // Find next free cell on target page
        val targetPageItems = items.filter { it.page == targetPage }
        val maxCols = _settings.value.gridCols
        val maxRows = _settings.value.gridRows
        var freeX = 0
        var freeY = 0
        var found = false
        for (y in 0 until maxRows) {
          for (x in 0 until maxCols) {
            if (targetPageItems.none { it.cellX == x && it.cellY == y }) {
              freeX = x
              freeY = y
              found = true
              break
            }
          }
          if (found) break
        }
        items[index] = item.copy(page = targetPage, cellX = freeX, cellY = freeY)
        _desktopItems.value = items
        saveDesktopItems(items)
      }
    }
  }

  fun shiftItemOrder(itemId: String, shiftForward: Boolean) {
    val items = _desktopItems.value.toMutableList()
    val current = items.find { it.id == itemId } ?: return
    val pageItems = items.filter { it.page == current.page && it.type != DesktopItemType.WIDGET }
      .sortedWith(compareBy({ it.cellY }, { it.cellX }))
    val idxInPage = pageItems.indexOfFirst { it.id == itemId }
    if (idxInPage == -1) return

    val targetIdx = if (shiftForward) idxInPage + 1 else idxInPage - 1
    if (targetIdx in pageItems.indices) {
      val targetItem = pageItems[targetIdx]
      swapDesktopItems(current.id, targetItem.id)
    }
  }

  fun addAppToExistingFolder(folderId: String, packageName: String) {
    val currentFolders = _folders.value
    val folder = currentFolders.find { it.id == folderId } ?: return
    if (!folder.packageNames.contains(packageName)) {
      val updatedPkgs = folder.packageNames + packageName
      updateFolder(folderId, folder.name, updatedPkgs)
    }
  }

  fun extractAppFromFolder(folderId: String, packageName: String, targetPage: Int = 0) {
    val currentFolders = _folders.value
    val folder = currentFolders.find { it.id == folderId } ?: return
    val updatedPkgs = folder.packageNames.filterNot { it == packageName }
    if (updatedPkgs.isEmpty()) {
      deleteFolder(folderId)
    } else {
      updateFolder(folderId, folder.name, updatedPkgs)
    }

    // Place app onto desktop
    val app = _installedApps.value.find { it.packageName == packageName }
    if (app != null) {
      val newItem = DesktopItem(
        id = UUID.randomUUID().toString(),
        type = DesktopItemType.APP,
        page = targetPage,
        cellX = 0,
        cellY = 4,
        packageName = app.packageName,
        activityName = app.activityName,
        label = app.label
      )
      addDesktopItem(newItem)
    }
  }

  fun combineAppsIntoFolder(targetAppItemId: String, droppedAppPackage: String, folderName: String = "Nueva Carpeta") {
    val items = _desktopItems.value.toMutableList()
    val targetItem = items.find { it.id == targetAppItemId } ?: return
    val targetPackage = targetItem.packageName ?: return

    // If target is already a folder
    if (targetItem.type == DesktopItemType.FOLDER && targetItem.folderId != null) {
      addAppToExistingFolder(targetItem.folderId, droppedAppPackage)
      // Remove any standalone desktop item for droppedAppPackage if present
      val standalone = items.find { it.packageName == droppedAppPackage && it.id != targetItem.id }
      if (standalone != null) {
        removeDesktopItem(standalone.id)
      }
      return
    }

    // Create new folder with both apps
    val pkgs = listOf(targetPackage, droppedAppPackage).distinct()
    val folder = FolderInfo(
      id = UUID.randomUUID().toString(),
      name = folderName,
      packageNames = pkgs
    )
    val updatedFolders = _folders.value + folder
    _folders.value = updatedFolders
    saveFolders(updatedFolders)

    // Replace targetItem with folder DesktopItem in place
    val index = items.indexOfFirst { it.id == targetAppItemId }
    if (index != -1) {
      items[index] = targetItem.copy(
        type = DesktopItemType.FOLDER,
        packageName = null,
        activityName = null,
        label = folder.name,
        folderId = folder.id,
        folderName = folder.name,
        folderApps = pkgs
      )
      // Remove dropped standalone item if it was on desktop
      val droppedItem = items.find { it.packageName == droppedAppPackage && it.id != targetAppItemId }
      if (droppedItem != null) {
        items.remove(droppedItem)
      }
      _desktopItems.value = items
      saveDesktopItems(items)
    }
  }

  fun updateDesktopItem(item: DesktopItem) {
    val updated = _desktopItems.value.map { if (it.id == item.id) item else it }
    _desktopItems.value = updated
    saveDesktopItems(updated)
  }

  fun setDockApps(pkgs: List<String>) {
    _dockApps.value = pkgs
    saveDockApps(pkgs)
  }

  fun addDockApp(pkg: String) {
    if (!_dockApps.value.contains(pkg)) {
      val updated = (_dockApps.value + pkg).take(_settings.value.dockItemCount)
      _dockApps.value = updated
      saveDockApps(updated)
    }
  }

  fun removeDockApp(pkg: String) {
    val updated = _dockApps.value.filterNot { it == pkg }
    _dockApps.value = updated
    saveDockApps(updated)
  }

  fun createFolder(name: String, packageNames: List<String>): FolderInfo {
    val folder = FolderInfo(
      id = UUID.randomUUID().toString(),
      name = name,
      packageNames = packageNames
    )
    val updatedFolders = _folders.value + folder
    _folders.value = updatedFolders
    saveFolders(updatedFolders)

    // Add folder item to desktop
    val desktopItem = DesktopItem(
      id = UUID.randomUUID().toString(),
      type = DesktopItemType.FOLDER,
      page = 0,
      cellX = 0,
      cellY = 4,
      folderId = folder.id,
      folderName = folder.name,
      folderApps = packageNames
    )
    addDesktopItem(desktopItem)
    return folder
  }

  fun updateFolder(folderId: String, newName: String, packageNames: List<String>) {
    val updatedFolders = _folders.value.map {
      if (it.id == folderId) it.copy(name = newName, packageNames = packageNames) else it
    }
    _folders.value = updatedFolders
    saveFolders(updatedFolders)

    // Also update desktop item
    val updatedDesktop = _desktopItems.value.map { item ->
      if (item.folderId == folderId) {
        item.copy(folderName = newName, folderApps = packageNames)
      } else item
    }
    _desktopItems.value = updatedDesktop
    saveDesktopItems(updatedDesktop)
  }

  fun deleteFolder(folderId: String) {
    val updatedFolders = _folders.value.filterNot { it.id == folderId }
    _folders.value = updatedFolders
    saveFolders(updatedFolders)

    val updatedDesktop = _desktopItems.value.filterNot { it.folderId == folderId }
    _desktopItems.value = updatedDesktop
    saveDesktopItems(updatedDesktop)
  }

  fun resetToDefaults() {
    _desktopItems.value = emptyList()
    _dockApps.value = emptyList()
    _folders.value = emptyList()
    saveSettings(LauncherSettings())
    seedDefaultDesktop(_installedApps.value)
  }

  private fun saveDesktopItems(items: List<DesktopItem>) {
    val array = JSONArray()
    for (item in items) {
      val obj = JSONObject().apply {
        put("id", item.id)
        put("type", item.type.name)
        put("page", item.page)
        put("cellX", item.cellX)
        put("cellY", item.cellY)
        put("spanX", item.spanX)
        put("spanY", item.spanY)
        put("packageName", item.packageName ?: "")
        put("activityName", item.activityName ?: "")
        put("label", item.label)
        put("folderId", item.folderId ?: "")
        put("folderName", item.folderName ?: "")
        val folderAppsArray = JSONArray()
        item.folderApps.forEach { folderAppsArray.put(it) }
        put("folderApps", folderAppsArray)
        put("widgetType", item.widgetType?.name ?: "")
      }
      array.put(obj)
    }
    prefs.edit().putString("desktop_items_json", array.toString()).apply()
  }

  private fun saveDockApps(pkgs: List<String>) {
    val array = JSONArray()
    pkgs.forEach { array.put(it) }
    prefs.edit().putString("dock_apps_json", array.toString()).apply()
  }

  private fun saveFolders(foldersList: List<FolderInfo>) {
    val array = JSONArray()
    for (f in foldersList) {
      val obj = JSONObject().apply {
        put("id", f.id)
        put("name", f.name)
        val arr = JSONArray()
        f.packageNames.forEach { arr.put(it) }
        put("apps", arr)
      }
      array.put(obj)
    }
    prefs.edit().putString("folders_json", array.toString()).apply()
  }

  private fun loadDesktopAndDock() {
    val desktopJson = prefs.getString("desktop_items_json", null)
    if (!desktopJson.isNullOrBlank()) {
      try {
        val array = JSONArray(desktopJson)
        val items = mutableListOf<DesktopItem>()
        for (i in 0 until array.length()) {
          val obj = array.getJSONObject(i)
          val folderAppsList = mutableListOf<String>()
          val folderAppsArr = obj.optJSONArray("folderApps")
          if (folderAppsArr != null) {
            for (j in 0 until folderAppsArr.length()) {
              folderAppsList.add(folderAppsArr.getString(j))
            }
          }
          items.add(
            DesktopItem(
              id = obj.getString("id"),
              type = DesktopItemType.valueOf(obj.getString("type")),
              page = obj.optInt("page", 0),
              cellX = obj.optInt("cellX", 0),
              cellY = obj.optInt("cellY", 0),
              spanX = obj.optInt("spanX", 1),
              spanY = obj.optInt("spanY", 1),
              packageName = obj.optString("packageName").ifBlank { null },
              activityName = obj.optString("activityName").ifBlank { null },
              label = obj.optString("label", ""),
              folderId = obj.optString("folderId").ifBlank { null },
              folderName = obj.optString("folderName").ifBlank { null },
              folderApps = folderAppsList,
              widgetType = obj.optString("widgetType").takeIf { it.isNotBlank() }?.let {
                try { WidgetType.valueOf(it) } catch (_: Exception) { null }
              }
            )
          )
        }
        _desktopItems.value = items
      } catch (_: Exception) {}
    }

    val dockJson = prefs.getString("dock_apps_json", null)
    if (!dockJson.isNullOrBlank()) {
      try {
        val array = JSONArray(dockJson)
        val dockList = mutableListOf<String>()
        for (i in 0 until array.length()) {
          dockList.add(array.getString(i))
        }
        _dockApps.value = dockList
      } catch (_: Exception) {}
    }

    val foldersJson = prefs.getString("folders_json", null)
    if (!foldersJson.isNullOrBlank()) {
      try {
        val array = JSONArray(foldersJson)
        val fList = mutableListOf<FolderInfo>()
        for (i in 0 until array.length()) {
          val obj = array.getJSONObject(i)
          val appArray = obj.optJSONArray("apps")
          val pkgs = mutableListOf<String>()
          if (appArray != null) {
            for (j in 0 until appArray.length()) {
              pkgs.add(appArray.getString(j))
            }
          }
          fList.add(
            FolderInfo(
              id = obj.getString("id"),
              name = obj.getString("name"),
              packageNames = pkgs
            )
          )
        }
        _folders.value = fList
      } catch (_: Exception) {}
    }
  }

  private fun drawableToBitmap(drawable: Drawable): Bitmap {
    if (drawable is BitmapDrawable && drawable.bitmap != null) {
      return drawable.bitmap
    }
    val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
    val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
  }
}
