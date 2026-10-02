package com.example.dreambyte.viewmodel

import android.app.Activity
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dreambyte.data.DeviceDetector
import com.example.dreambyte.data.LauncherRepository
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class LauncherViewModel(private val repository: LauncherRepository) : ViewModel() {

  val installedApps: StateFlow<List<AppInfo>> = repository.installedApps
  val desktopItems: StateFlow<List<DesktopItem>> = repository.desktopItems
  val dockApps: StateFlow<List<String>> = repository.dockApps
  val folders: StateFlow<List<FolderInfo>> = repository.folders
  val settings: StateFlow<LauncherSettings> = repository.settings
  val deviceMetrics: StateFlow<DeviceMetrics> = repository.deviceMetrics

  // UI States
  private val _isDrawerOpen = MutableStateFlow(false)
  val isDrawerOpen: StateFlow<Boolean> = _isDrawerOpen.asStateFlow()

  private val _isSettingsOpen = MutableStateFlow(false)
  val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

  private val _isHudOpen = MutableStateFlow(false)
  val isHudOpen: StateFlow<Boolean> = _isHudOpen.asStateFlow()

  private val _isWelcomeOpen = MutableStateFlow(false)
  val isWelcomeOpen: StateFlow<Boolean> = _isWelcomeOpen.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedAppContext = MutableStateFlow<AppInfo?>(null)
  val selectedAppContext: StateFlow<AppInfo?> = _selectedAppContext.asStateFlow()

  private val _selectedFolder = MutableStateFlow<FolderInfo?>(null)
  val selectedFolder: StateFlow<FolderInfo?> = _selectedFolder.asStateFlow()

  private val _isDefaultLauncher = MutableStateFlow(false)
  val isDefaultLauncher: StateFlow<Boolean> = _isDefaultLauncher.asStateFlow()

  // Edit / Reorganize Mode
  private val _isEditMode = MutableStateFlow(false)
  val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

  private val _selectedMoveItemId = MutableStateFlow<String?>(null)
  val selectedMoveItemId: StateFlow<String?> = _selectedMoveItemId.asStateFlow()

  // Filtered apps for drawer
  val filteredApps: StateFlow<List<AppInfo>> = combine(installedApps, _searchQuery) { apps, query ->
    if (query.isBlank()) {
      apps
    } else {
      apps.filter {
        it.label.contains(query, ignoreCase = true) ||
          it.packageName.contains(query, ignoreCase = true)
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    viewModelScope.launch {
      if (!settings.value.firstLaunchCompleted) {
        _isWelcomeOpen.value = true
      }
    }
  }

  fun checkDefaultLauncherStatus(context: Context) {
    try {
      val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
      val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
      val defaultPkg = resolveInfo?.activityInfo?.packageName
      _isDefaultLauncher.value = defaultPkg == context.packageName
    } catch (_: Exception) {
      _isDefaultLauncher.value = false
    }
  }

  fun requestSetDefaultLauncher(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      val roleManager = activity.getSystemService(RoleManager::class.java)
      if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
        if (!roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
          val roleIntent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
          activity.startActivity(roleIntent)
          return
        }
      }
    }

    try {
      val intent = Intent(Settings.ACTION_HOME_SETTINGS)
      activity.startActivity(intent)
    } catch (_: Exception) {
      try {
        val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        activity.startActivity(intent)
      } catch (_: Exception) {
        Toast.makeText(activity, "Abre Configuración -> Apps predeterminadas -> Inicio", Toast.LENGTH_LONG).show()
      }
    }
  }

  fun launchApp(context: Context, app: AppInfo) {
    try {
      val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
      if (intent != null) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
      } else {
        val explicitIntent = Intent().apply {
          component = ComponentName(app.packageName, app.activityName)
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(explicitIntent)
      }
    } catch (e: Exception) {
      Toast.makeText(context, "No se pudo abrir ${app.label}: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
  }

  fun launchPackage(context: Context, packageName: String) {
    val app = installedApps.value.find { it.packageName == packageName }
    if (app != null) {
      launchApp(context, app)
    } else {
      try {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          context.startActivity(intent)
        }
      } catch (_: Exception) {}
    }
  }

  fun openAppInfo(context: Context, packageName: String) {
    try {
      val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (_: Exception) {
      Toast.makeText(context, "Error abriendo ajustes de app", Toast.LENGTH_SHORT).show()
    }
  }

  fun requestUninstall(context: Context, packageName: String) {
    try {
      val intent = Intent(Intent.ACTION_DELETE).apply {
        data = Uri.fromParts("package", packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (_: Exception) {
      Toast.makeText(context, "Error solicitando desinstalación", Toast.LENGTH_SHORT).show()
    }
  }

  // Drawer & Sheets
  fun openDrawer() { _isDrawerOpen.value = true }
  fun closeDrawer() {
    _isDrawerOpen.value = false
    _searchQuery.value = ""
  }
  fun updateSearchQuery(query: String) { _searchQuery.value = query }

  fun openSettings() { _isSettingsOpen.value = true }
  fun closeSettings() { _isSettingsOpen.value = false }

  fun openHud() { _isHudOpen.value = true }
  fun closeHud() { _isHudOpen.value = false }

  fun setCustomWallpaper(filePath: String) {
    repository.saveSettings(
      settings.value.copy(
        wallpaperType = WallpaperType.CUSTOM,
        customWallpaperPath = filePath
      )
    )
  }

  fun toggleInteractiveParticles(enabled: Boolean) {
    repository.saveSettings(
      settings.value.copy(interactiveParticlesEnabled = enabled)
    )
  }

  fun openWelcome() { _isWelcomeOpen.value = true }
  fun closeWelcome() {
    _isWelcomeOpen.value = false
    repository.saveSettings(settings.value.copy(firstLaunchCompleted = true))
  }

  fun openAppContext(app: AppInfo) { _selectedAppContext.value = app }
  fun closeAppContext() { _selectedAppContext.value = null }

  fun openFolder(folder: FolderInfo) { _selectedFolder.value = folder }
  fun closeFolder() { _selectedFolder.value = null }

  // Customization settings
  fun setAppearance(appearance: DreamByteAppearance) {
    repository.saveSettings(settings.value.copy(appearance = appearance))
  }

  fun setWallpaperType(wallpaperType: WallpaperType) {
    repository.saveSettings(settings.value.copy(wallpaperType = wallpaperType))
  }

  fun setGridDimensions(rows: Int, cols: Int) {
    repository.saveSettings(settings.value.copy(gridRows = rows, gridCols = cols))
  }

  fun setIconSize(size: IconSize) {
    repository.saveSettings(settings.value.copy(iconSize = size))
  }

  fun toggleAnimations(enabled: Boolean) {
    repository.saveSettings(settings.value.copy(animationsEnabled = enabled))
  }

  fun toggleDreamBot(show: Boolean) {
    repository.saveSettings(settings.value.copy(showDreamBot = show))
  }

  fun setDeviceModeOverride(deviceType: DeviceType) {
    repository.saveSettings(settings.value.copy(deviceModeOverride = deviceType))
  }

  fun setDrawerLayout(mode: DrawerLayoutMode) {
    repository.saveSettings(settings.value.copy(drawerLayout = mode))
  }

  // Desktop item manipulation
  fun addAppToDesktop(app: AppInfo, page: Int = 0) {
    val itemsOnPage = desktopItems.value.filter { it.page == page }
    val maxCols = settings.value.gridCols
    val maxRows = settings.value.gridRows

    // Find first free cell
    var targetX = 0
    var targetY = 0
    var found = false
    for (y in 0 until maxRows) {
      for (x in 0 until maxCols) {
        val occupied = itemsOnPage.any { it.cellX == x && it.cellY == y }
        if (!occupied) {
          targetX = x
          targetY = y
          found = true
          break
        }
      }
      if (found) break
    }

    val newItem = DesktopItem(
      id = UUID.randomUUID().toString(),
      type = DesktopItemType.APP,
      page = page,
      cellX = targetX,
      cellY = targetY,
      packageName = app.packageName,
      activityName = app.activityName,
      label = app.label
    )
    repository.addDesktopItem(newItem)
  }

  fun removeDesktopItem(itemId: String) {
    repository.removeDesktopItem(itemId)
  }

  fun toggleDockApp(packageName: String) {
    if (dockApps.value.contains(packageName)) {
      repository.removeDockApp(packageName)
    } else {
      repository.addDockApp(packageName)
    }
  }

  fun createFolderWithApp(app: AppInfo, folderName: String = "Carpeta") {
    val folder = repository.createFolder(folderName, listOf(app.packageName))
    _selectedFolder.value = folder
  }

  fun renameFolder(folderId: String, newName: String) {
    val current = folders.value.find { it.id == folderId } ?: return
    repository.updateFolder(folderId, newName, current.packageNames)
  }

  fun deleteFolder(folderId: String) {
    repository.deleteFolder(folderId)
    closeFolder()
  }

  fun enterEditMode(itemId: String? = null) {
    _isEditMode.value = true
    _selectedMoveItemId.value = itemId
  }

  fun exitEditMode() {
    _isEditMode.value = false
    _selectedMoveItemId.value = null
  }

  fun selectItemToMove(itemId: String?) {
    _selectedMoveItemId.value = itemId
  }

  fun moveItemLeft(itemId: String) {
    repository.shiftItemOrder(itemId, shiftForward = false)
  }

  fun moveItemRight(itemId: String) {
    repository.shiftItemOrder(itemId, shiftForward = true)
  }

  fun moveItemToPage(itemId: String, targetPage: Int) {
    repository.moveItemToPage(itemId, targetPage)
  }

  fun swapItems(idA: String, idB: String) {
    repository.swapDesktopItems(idA, idB)
  }

  fun combineAppsIntoFolder(targetItemId: String, droppedAppPackage: String, folderName: String = "Nueva Carpeta") {
    repository.combineAppsIntoFolder(targetItemId, droppedAppPackage, folderName)
  }

  fun extractAppFromFolder(folderId: String, packageName: String, targetPage: Int = 0) {
    repository.extractAppFromFolder(folderId, packageName, targetPage)
    // Update local selectedFolder if still open
    val updatedFolder = folders.value.find { it.id == folderId }
    if (updatedFolder == null || updatedFolder.packageNames.isEmpty()) {
      closeFolder()
    } else {
      _selectedFolder.value = updatedFolder
    }
  }

  fun resetAllSettings() {
    repository.resetToDefaults()
  }

  fun refreshData() {
    repository.loadInstalledApps()
    repository.refreshHardwareMetrics()
  }

  override fun onCleared() {
    super.onCleared()
    repository.unregisterReceivers()
  }
}
