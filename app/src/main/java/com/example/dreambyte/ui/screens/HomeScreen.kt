package com.example.dreambyte.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreambyte.data.DeviceDetector
import com.example.dreambyte.model.AppInfo
import com.example.dreambyte.model.DesktopItem
import com.example.dreambyte.model.DesktopItemType
import com.example.dreambyte.model.DeviceType
import com.example.dreambyte.model.DreamByteAppearance
import com.example.dreambyte.model.FolderInfo
import com.example.dreambyte.model.WidgetType
import com.example.dreambyte.ui.components.AppContextMenu
import com.example.dreambyte.ui.components.AppIconItem
import com.example.dreambyte.ui.components.DreamBotAvatar
import com.example.dreambyte.ui.components.DreamBotCompanionCard
import com.example.dreambyte.ui.components.DreamByteHudSheet
import com.example.dreambyte.ui.components.FolderDialog
import com.example.dreambyte.ui.components.GlassCard
import com.example.dreambyte.ui.components.WallpaperBackground
import com.example.dreambyte.ui.components.WidgetPickerDialog
import com.example.dreambyte.ui.theme.LocalDreamByteStyle
import com.example.dreambyte.ui.widgets.DreamByteBatteryWidget
import com.example.dreambyte.ui.widgets.DreamByteClockWidget
import com.example.dreambyte.ui.widgets.DreamByteDeviceInfoWidget
import com.example.dreambyte.ui.widgets.DreamByteMediaWidget
import com.example.dreambyte.ui.widgets.DreamByteNotesWidget
import com.example.dreambyte.ui.widgets.DreamByteSearchWidget
import com.example.dreambyte.ui.widgets.DreamByteWeatherWidget
import com.example.dreambyte.viewmodel.LauncherViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
  viewModel: LauncherViewModel,
  activity: Activity
) {
  val context = LocalContext.current
  val style = LocalDreamByteStyle.current
  val coroutineScope = rememberCoroutineScope()

  val installedApps by viewModel.installedApps.collectAsState()
  val appsMap: Map<String, AppInfo> = remember(installedApps) { installedApps.associateBy { it.packageName } }
  val desktopItems by viewModel.desktopItems.collectAsState()
  val dockApps by viewModel.dockApps.collectAsState()
  val folders by viewModel.folders.collectAsState()
  val settings by viewModel.settings.collectAsState()
  val deviceMetrics by viewModel.deviceMetrics.collectAsState()

  val isDrawerOpen by viewModel.isDrawerOpen.collectAsState()
  val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
  val isWelcomeOpen by viewModel.isWelcomeOpen.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val filteredApps by viewModel.filteredApps.collectAsState()
  val selectedAppContext by viewModel.selectedAppContext.collectAsState()
  val selectedFolder by viewModel.selectedFolder.collectAsState()
  val isDefaultLauncher by viewModel.isDefaultLauncher.collectAsState()

  val isEditMode by viewModel.isEditMode.collectAsState()
  val selectedMoveItemId by viewModel.selectedMoveItemId.collectAsState()
  val isHudOpen by viewModel.isHudOpen.collectAsState()
  val isWidgetPickerOpen by viewModel.isWidgetPickerOpen.collectAsState()

  val detectedDevice = DeviceDetector.currentDevice(settings.deviceModeOverride)

  LaunchedEffect(Unit) {
    viewModel.checkDefaultLauncherStatus(context)
  }

  // Pager for desktop pages (pages 0 and 1)
  val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

  Box(
    modifier = Modifier.fillMaxSize()
  ) {
    // Wallpaper Layer
    WallpaperBackground(
      wallpaperType = settings.wallpaperType,
      customWallpaperPath = settings.customWallpaperPath,
      interactiveParticles = settings.interactiveParticlesEnabled
    )

    // Main Desktop Column
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
    ) {
      // Top DreamByte System Bar
      TopDreamByteSystemBar(
        deviceType = detectedDevice,
        batteryLevel = deviceMetrics.batteryLevel,
        isCharging = deviceMetrics.isCharging,
        isEditMode = isEditMode,
        onToggleEditMode = {
          if (isEditMode) viewModel.exitEditMode() else viewModel.enterEditMode()
        },
        onOpenWidgetPicker = { viewModel.openWidgetPicker() },
        onOpenDrawer = { viewModel.openDrawer() },
        onOpenHud = { viewModel.openHud() },
        onOpenSettings = { viewModel.openSettings() }
      )

      // Edit Mode Active Banner
      AnimatedVisibility(visible = isEditMode) {
        val selectedItem = desktopItems.find { it.id == selectedMoveItemId }
        EditModeTopBanner(
          selectedItem = selectedItem,
          onMoveToOtherPage = { id ->
            val target = if (selectedItem?.page == 0) 1 else 0
            viewModel.moveItemToPage(id, target)
            coroutineScope.launch { pagerState.animateScrollToPage(target) }
          },
          onRemoveItem = { id ->
            viewModel.removeDesktopItem(id)
            viewModel.selectItemToMove(null)
          },
          onOpenWidgetPicker = { viewModel.openWidgetPicker() },
          onDone = { viewModel.exitEditMode() }
        )
      }

      // Desktop Pager
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
      ) {
        HorizontalPager(
          state = pagerState,
          modifier = Modifier.fillMaxSize()
        ) { page ->
          DesktopPageContent(
            pageIndex = page,
            desktopItems = desktopItems.filter { it.page == page },
            appsMap = appsMap,
            folders = folders,
            metrics = deviceMetrics,
            deviceType = detectedDevice,
            isEditMode = isEditMode,
            selectedMoveItemId = selectedMoveItemId,
            onSelectItem = { itemId ->
              if (selectedMoveItemId != null && selectedMoveItemId != itemId) {
                // Instantly swap them freely!
                viewModel.swapItems(selectedMoveItemId!!, itemId)
                viewModel.selectItemToMove(null)
              } else {
                viewModel.selectItemToMove(if (selectedMoveItemId == itemId) null else itemId)
              }
            },
            onMoveToOtherPage = { itemId ->
              val target = if (page == 0) 1 else 0
              viewModel.moveItemToPage(itemId, target)
              coroutineScope.launch { pagerState.animateScrollToPage(target) }
            },
            onRemoveItem = { itemId -> viewModel.removeDesktopItem(itemId) },
            onLaunchApp = { app ->
              if (isEditMode) {
                val item = desktopItems.find { it.packageName == app.packageName }
                if (item != null) {
                  if (selectedMoveItemId != null && selectedMoveItemId != item.id) {
                    viewModel.swapItems(selectedMoveItemId!!, item.id)
                    viewModel.selectItemToMove(null)
                  } else {
                    viewModel.selectItemToMove(if (selectedMoveItemId == item.id) null else item.id)
                  }
                }
              } else {
                viewModel.launchApp(context, app)
              }
            },
            onLaunchPackage = { pkg ->
              if (!isEditMode) {
                viewModel.launchPackage(context, pkg)
              }
            },
            onLongClickApp = { app ->
              if (!isEditMode) {
                viewModel.openAppContext(app)
              }
            },
            onOpenFolder = { folder ->
              if (isEditMode) {
                val item = desktopItems.find { it.folderId == folder.id }
                if (item != null) {
                  if (selectedMoveItemId != null && selectedMoveItemId != item.id) {
                    viewModel.swapItems(selectedMoveItemId!!, item.id)
                    viewModel.selectItemToMove(null)
                  } else {
                    viewModel.selectItemToMove(if (selectedMoveItemId == item.id) null else item.id)
                  }
                }
              } else {
                viewModel.openFolder(folder)
              }
            },
            onOpenSettings = { viewModel.openSettings() },
            onOpenDrawer = { viewModel.openDrawer() },
            onOpenHud = { viewModel.openHud() },
            onOpenWidgetPicker = { viewModel.openWidgetPicker() }
          )
        }
      }

      // Page Indicator Dots
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 2.dp)
      ) {
        repeat(2) { index ->
          val isCurrent = pagerState.currentPage == index
          Box(
            modifier = Modifier
              .padding(horizontal = 4.dp)
              .size(if (isCurrent) 18.dp else 6.dp, 6.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(if (isCurrent) style.primaryHolo else style.textMuted.copy(alpha = 0.4f))
              .clickable {
                coroutineScope.launch { pagerState.animateScrollToPage(index) }
              }
          )
        }
      }

      // Drawer Pull Handle (Visible, interactive drawer tab)
      DrawerHandleTab(
        onOpenDrawer = { viewModel.openDrawer() }
      )

      // Bottom Dock
      if (detectedDevice == DeviceType.DREAM_PAD) {
        DreamPadTaskbar(
          dockApps = dockApps,
          appsMap = appsMap,
          batteryLevel = deviceMetrics.batteryLevel,
          onOpenDrawer = { viewModel.openDrawer() },
          onOpenSettings = { viewModel.openSettings() },
          onLaunchApp = { app -> viewModel.launchApp(context, app) },
          onLaunchPackage = { pkg -> viewModel.launchPackage(context, pkg) }
        )
      } else {
        DreamByteDock(
          dockApps = dockApps,
          appsMap = appsMap,
          deviceType = detectedDevice,
          onOpenDrawer = { viewModel.openDrawer() },
          onOpenSettings = { viewModel.openSettings() },
          onLaunchApp = { app -> viewModel.launchApp(context, app) },
          onLongClickApp = { app -> viewModel.openAppContext(app) },
          onLaunchPackage = { pkg -> viewModel.launchPackage(context, pkg) }
        )
      }
    }

    // App Drawer Overlay (Animated slide-in)
    AppDrawerSheet(
      isOpen = isDrawerOpen,
      apps = filteredApps,
      searchQuery = searchQuery,
      layoutMode = settings.drawerLayout,
      iconSize = settings.iconSize,
      gridCols = if (detectedDevice == DeviceType.PHONE) 4 else 6,
      onQueryChange = { viewModel.updateSearchQuery(it) },
      onClose = { viewModel.closeDrawer() },
      onLaunchApp = { app ->
        viewModel.closeDrawer()
        viewModel.launchApp(context, app)
      },
      onLongClickApp = { app ->
        viewModel.openAppContext(app)
      },
      onToggleLayoutMode = {
        viewModel.setDrawerLayout(
          if (settings.drawerLayout == com.example.dreambyte.model.DrawerLayoutMode.GRID)
            com.example.dreambyte.model.DrawerLayoutMode.LIST
          else
            com.example.dreambyte.model.DrawerLayoutMode.GRID
        )
      }
    )

    // Settings Screen Overlay
    if (isSettingsOpen) {
      SettingsScreen(
        settings = settings,
        isDefaultLauncher = isDefaultLauncher,
        onBack = {
          viewModel.closeSettings()
          viewModel.checkDefaultLauncherStatus(context)
        },
        onSetAppearance = { viewModel.setAppearance(it) },
        onSetWallpaper = { viewModel.setWallpaperType(it) },
        onSetCustomWallpaper = { viewModel.setCustomWallpaper(it) },
        onToggleInteractiveParticles = { viewModel.toggleInteractiveParticles(it) },
        onSetGrid = { r, c -> viewModel.setGridDimensions(r, c) },
        onSetIconSize = { viewModel.setIconSize(it) },
        onToggleAnimations = { viewModel.toggleAnimations(it) },
        onToggleDreamBot = { viewModel.toggleDreamBot(it) },
        onSetDeviceMode = { viewModel.setDeviceModeOverride(it) },
        onOpenHud = { viewModel.openHud() },
        onRequestSetDefault = { viewModel.requestSetDefaultLauncher(activity) },
        onResetDefaults = { viewModel.resetAllSettings() }
      )
    }

    // DreamByte Holographic Command Center & HUD Overlay
    DreamByteHudSheet(
      isOpen = isHudOpen,
      metrics = deviceMetrics,
      interactiveParticles = settings.interactiveParticlesEnabled,
      onToggleInteractiveParticles = { viewModel.toggleInteractiveParticles(it) },
      onClose = { viewModel.closeHud() },
      onOpenSettings = {
        viewModel.closeHud()
        viewModel.openSettings()
      }
    )

    // Welcome Screen
    if (isWelcomeOpen) {
      WelcomeSheet(onGetStarted = { viewModel.closeWelcome() })
    }

    // Folder Dialog Overlay
    selectedFolder?.let { folder ->
      val appsInFolder = folder.packageNames.mapNotNull { pkg ->
        appsMap[pkg]
      }
      FolderDialog(
        folder = folder,
        appsInFolder = appsInFolder,
        onDismiss = { viewModel.closeFolder() },
        onLaunchApp = { app -> viewModel.launchApp(context, app) },
        onRenameFolder = { newName -> viewModel.renameFolder(folder.id, newName) },
        onDeleteFolder = { viewModel.deleteFolder(folder.id) },
        onExtractApp = { pkg ->
          viewModel.extractAppFromFolder(folder.id, pkg, pagerState.currentPage)
        }
      )
    }

    // App Context Menu Overlay
    selectedAppContext?.let { app ->
      val isOnDesktop = desktopItems.any { it.packageName == app.packageName }
      val isDocked = dockApps.contains(app.packageName)
      AppContextMenu(
        app = app,
        isOnDesktop = isOnDesktop,
        isDocked = isDocked,
        onDismiss = { viewModel.closeAppContext() },
        onAddToDesktop = { viewModel.addAppToDesktop(app, pagerState.currentPage) },
        onRemoveFromDesktop = {
          val item = desktopItems.find { it.packageName == app.packageName }
          if (item != null) viewModel.removeDesktopItem(item.id)
        },
        onToggleDock = { viewModel.toggleDockApp(app.packageName) },
        onCreateFolder = { viewModel.createFolderWithApp(app, "Carpeta de ${app.label}") },
        onMoveItem = {
          val item = desktopItems.find { it.packageName == app.packageName }
          viewModel.enterEditMode(item?.id)
        },
        onAppInfo = { viewModel.openAppInfo(context, app.packageName) },
        onUninstall = { viewModel.requestUninstall(context, app.packageName) }
      )
    }

    // Widget Picker Dialog Overlay
    if (isWidgetPickerOpen) {
      WidgetPickerDialog(
        currentPage = pagerState.currentPage,
        onDismiss = { viewModel.closeWidgetPicker() },
        onSelectWidget = { widgetType ->
          viewModel.addWidgetToDesktop(widgetType, pagerState.currentPage)
        }
      )
    }
  }
}

@Composable
private fun DrawerHandleTab(
  onOpenDrawer: () -> Unit
) {
  val style = LocalDreamByteStyle.current
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 2.dp, bottom = 4.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(style.surfaceGlass)
        .border(0.8.dp, style.borderHighlight, RoundedCornerShape(16.dp))
        .clickable(onClick = onOpenDrawer)
        .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
      Icon(
        imageVector = Icons.Default.KeyboardArrowUp,
        contentDescription = "Abrir Cajón de Aplicaciones",
        tint = style.primaryHolo,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "APLICACIONES",
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = style.textSecondary
      )
    }
  }
}

@Composable
private fun TopDreamByteSystemBar(
  deviceType: DeviceType,
  batteryLevel: Int,
  isCharging: Boolean,
  isEditMode: Boolean,
  onToggleEditMode: () -> Unit,
  onOpenWidgetPicker: () -> Unit,
  onOpenDrawer: () -> Unit,
  onOpenHud: () -> Unit,
  onOpenSettings: () -> Unit
) {
  val style = LocalDreamByteStyle.current
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp)
  ) {
    // Left: DreamByte Brand & Device Type (Clicking brand opens HUD Command Center!)
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .clickable(onClick = onOpenHud)
        .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
      Text(
        text = "DREAMBYTE",
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp,
        color = style.primaryHolo
      )
      Spacer(modifier = Modifier.width(4.dp))
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(4.dp))
          .background(style.primaryHolo.copy(alpha = 0.2f))
          .padding(horizontal = 4.dp, vertical = 1.dp)
      ) {
        Text(
          text = deviceType.displayName,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = style.textSecondary
        )
      }
    }

    // Right: Action Buttons (Organizar, +Widget, HUD, Cajón de Apps, Batería, Settings)
    Row(verticalAlignment = Alignment.CenterVertically) {
      // Reorganize button
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(10.dp))
          .background(if (isEditMode) style.primaryHolo else style.surfaceGlass)
          .border(0.8.dp, style.borderHighlight, RoundedCornerShape(10.dp))
          .clickable(onClick = onToggleEditMode)
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (isEditMode) Icons.Default.Check else Icons.Default.OpenWith,
            contentDescription = "Modo Organizar",
            tint = if (isEditMode) Color(0xFF020D1D) else style.primaryHolo,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isEditMode) "Listo" else "Mover",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isEditMode) Color(0xFF020D1D) else style.textPrimary
          )
        }
      }

      Spacer(modifier = Modifier.width(4.dp))

      // Add Widget button
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(10.dp))
          .background(style.surfaceGlass)
          .border(0.8.dp, style.borderHighlight, RoundedCornerShape(10.dp))
          .clickable(onClick = onOpenWidgetPicker)
          .padding(horizontal = 6.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Agregar Widget",
            tint = style.primaryHolo,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(2.dp))
          Text(
            text = "Widget",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = style.textPrimary
          )
        }
      }

      Spacer(modifier = Modifier.width(4.dp))

      // HUD / Command Center quick button
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(10.dp))
          .background(style.surfaceGlass)
          .border(0.8.dp, style.borderHighlight, RoundedCornerShape(10.dp))
          .clickable(onClick = onOpenHud)
          .padding(horizontal = 6.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = "HUD",
            tint = style.primaryHolo,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(2.dp))
          Text(
            text = "HUD",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = style.textPrimary
          )
        }
      }

      Spacer(modifier = Modifier.width(4.dp))

      // Drawer quick button
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(10.dp))
          .background(style.surfaceGlass)
          .border(0.8.dp, style.borderHighlight, RoundedCornerShape(10.dp))
          .clickable(onClick = onOpenDrawer)
          .padding(horizontal = 6.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Apps,
            contentDescription = "Cajón",
            tint = style.primaryHolo,
            modifier = Modifier.size(13.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(4.dp))

      // Battery Pill
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(10.dp))
          .background(style.surfaceGlass)
          .border(0.8.dp, style.borderHighlight, RoundedCornerShape(10.dp))
          .padding(horizontal = 6.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (isCharging) Icons.Default.Bolt else Icons.Default.BatteryFull,
            contentDescription = null,
            tint = if (isCharging) style.primaryHolo else style.textSecondary,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(2.dp))
          Text(
            text = "$batteryLevel%",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = style.textPrimary
          )
        }
      }

      IconButton(
        onClick = onOpenSettings,
        modifier = Modifier.size(28.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Settings,
          contentDescription = "Configuración",
          tint = style.textSecondary,
          modifier = Modifier.size(17.dp)
        )
      }
    }
  }
}

@Composable
private fun EditModeTopBanner(
  selectedItem: DesktopItem?,
  onMoveToOtherPage: (String) -> Unit,
  onRemoveItem: (String) -> Unit,
  onOpenWidgetPicker: () -> Unit,
  onDone: () -> Unit
) {
  val style = LocalDreamByteStyle.current
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 4.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(style.primaryHolo.copy(alpha = 0.15f))
      .border(1.dp, style.primaryHolo, RoundedCornerShape(12.dp))
      .padding(horizontal = 12.dp, vertical = 6.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = if (selectedItem != null) "Moviendo: ${selectedItem.label.ifBlank { selectedItem.folderName ?: selectedItem.widgetType?.displayName ?: "Elemento" }}" else "Modo Organizar Libre",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = style.primaryHolo
          )
          Text(
            text = if (selectedItem != null) "Toca cualquier otro elemento o posición para intercambiar su lugar" else "Toca cualquier app, carpeta o widget para moverlo libremente",
            fontSize = 10.5.sp,
            color = style.textPrimary
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(style.surfaceGlass)
              .border(0.8.dp, style.borderHighlight, RoundedCornerShape(8.dp))
              .clickable(onClick = onOpenWidgetPicker)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = style.primaryHolo,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "Widget",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = style.primaryHolo
              )
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(style.primaryHolo)
              .clickable(onClick = onDone)
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "Listo",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF020D1D)
            )
          }
        }
      }

      if (selectedItem != null) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val targetPage = if (selectedItem.page == 0) 1 else 0
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(style.primaryHolo.copy(alpha = 0.25f))
              .border(0.8.dp, style.primaryHolo, RoundedCornerShape(8.dp))
              .clickable { onMoveToOtherPage(selectedItem.id) }
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = null,
                tint = style.primaryHolo,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Mover a Página ${targetPage + 1}",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = style.primaryHolo
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFEF4444).copy(alpha = 0.2f))
              .border(0.8.dp, Color(0xFFEF4444), RoundedCornerShape(8.dp))
              .clickable { onRemoveItem(selectedItem.id) }
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Quitar",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEF4444)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun DesktopPageContent(
  pageIndex: Int,
  desktopItems: List<DesktopItem>,
  appsMap: Map<String, AppInfo>,
  folders: List<FolderInfo>,
  metrics: com.example.dreambyte.model.DeviceMetrics,
  deviceType: DeviceType,
  isEditMode: Boolean,
  selectedMoveItemId: String?,
  onSelectItem: (String) -> Unit,
  onMoveToOtherPage: (String) -> Unit,
  onRemoveItem: (String) -> Unit,
  onLaunchApp: (AppInfo) -> Unit,
  onLaunchPackage: (String) -> Unit,
  onLongClickApp: (AppInfo) -> Unit,
  onOpenFolder: (FolderInfo) -> Unit,
  onOpenSettings: () -> Unit,
  onOpenDrawer: () -> Unit,
  onOpenHud: () -> Unit,
  onOpenWidgetPicker: () -> Unit
) {
  val style = LocalDreamByteStyle.current
  val scrollState = rememberScrollState()

  // Partition items into widgets and apps/folders
  val widgets = desktopItems.filter { it.type == DesktopItemType.WIDGET }
  val appItems = desktopItems.filter { it.type == DesktopItemType.APP || it.type == DesktopItemType.FOLDER }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 14.dp, vertical = 4.dp)
  ) {
    // 1. Render all widgets for this page
    if (widgets.isNotEmpty()) {
      widgets.forEach { widget ->
        val isSelected = isEditMode && selectedMoveItemId == widget.id
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .animateContentSize()
            .then(
              if (isEditMode) {
                Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .background(
                    if (isSelected) style.primaryHolo.copy(alpha = 0.2f)
                    else style.surfaceGlass.copy(alpha = 0.3f)
                  )
                  .border(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) style.primaryHolo else style.borderHighlight.copy(alpha = 0.5f),
                    RoundedCornerShape(16.dp)
                  )
                  .clickable { onSelectItem(widget.id) }
                  .padding(4.dp)
              } else Modifier
            )
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            // Edit mode header bar for widget
            if (isEditMode) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Widgets,
                    contentDescription = null,
                    tint = if (isSelected) style.primaryHolo else style.textSecondary,
                    modifier = Modifier.size(13.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = widget.widgetType?.displayName ?: "Widget",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) style.primaryHolo else style.textSecondary
                  )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  // Move to other page
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(style.primaryHolo.copy(alpha = 0.25f))
                      .clickable { onMoveToOtherPage(widget.id) }
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "A Pág ${if (pageIndex == 0) 2 else 1}",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = style.primaryHolo
                    )
                  }

                  Spacer(modifier = Modifier.width(6.dp))

                  // Remove widget
                  Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                      .size(20.dp)
                      .clip(CircleShape)
                      .background(Color(0xFFEF4444))
                      .clickable { onRemoveItem(widget.id) }
                  ) {
                    Icon(
                      imageVector = Icons.Default.Close,
                      contentDescription = "Quitar widget",
                      tint = Color.White,
                      modifier = Modifier.size(12.dp)
                    )
                  }
                }
              }
              Spacer(modifier = Modifier.height(2.dp))
            }

            // Real widget content
            when (widget.widgetType) {
              WidgetType.CLOCK -> DreamByteClockWidget(onOpenClockApp = onOpenSettings)
              WidgetType.WEATHER -> DreamByteWeatherWidget()
              WidgetType.BATTERY -> DreamByteBatteryWidget(metrics = metrics)
              WidgetType.DEVICE_INFO -> DreamByteDeviceInfoWidget(metrics = metrics)
              WidgetType.MEDIA_PLAYER -> DreamByteMediaWidget()
              WidgetType.QUICK_NOTES -> DreamByteNotesWidget()
              WidgetType.QUICK_SEARCH -> DreamByteSearchWidget(onOpenSearch = onOpenDrawer, onOpenHud = onOpenHud)
              WidgetType.DREAMBOT -> DreamBotCompanionCard(onOpenSettings = onOpenSettings)
              null -> DreamByteClockWidget(onOpenClockApp = onOpenSettings)
            }
          }
        }
      }
    } else {
      // Default initial layout if no widgets on this page yet
      if (pageIndex == 0) {
        DreamByteClockWidget(onOpenClockApp = onOpenSettings)
        Spacer(modifier = Modifier.height(8.dp))
        DreamBotCompanionCard(onOpenSettings = onOpenSettings)
      } else {
        DreamByteWeatherWidget()
        Spacer(modifier = Modifier.height(8.dp))
        DreamByteBatteryWidget(metrics = metrics)
        Spacer(modifier = Modifier.height(8.dp))
        DreamByteDeviceInfoWidget(metrics = metrics)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 2. Render Apps & Folders Grid
    if (appItems.isNotEmpty()) {
      val cols = if (deviceType == DeviceType.PHONE) 4 else 6
      val rows = (appItems.size + cols - 1) / cols

      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        for (rowIndex in 0 until rows) {
          Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
          ) {
            for (colIndex in 0 until cols) {
              val itemIndex = rowIndex * cols + colIndex
              if (itemIndex < appItems.size) {
                val item = appItems[itemIndex]
                val isSelected = isEditMode && selectedMoveItemId == item.id

                Box(
                  modifier = Modifier
                    .weight(1f)
                    .then(
                      if (isEditMode) {
                        Modifier
                          .clip(RoundedCornerShape(14.dp))
                          .background(
                            if (isSelected) style.primaryHolo.copy(alpha = 0.28f)
                            else style.surfaceGlass.copy(alpha = 0.35f)
                          )
                          .border(
                            if (isSelected) 2.dp else 0.8.dp,
                            if (isSelected) style.primaryHolo else style.borderHighlight.copy(alpha = 0.4f),
                            RoundedCornerShape(14.dp)
                          )
                      } else Modifier
                    )
                    .padding(vertical = 4.dp, horizontal = 2.dp)
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (item.type == DesktopItemType.APP) {
                      val app = appsMap[item.packageName]
                      val displayLabel = app?.label ?: item.label.ifBlank { "App" }
                      val displayIcon = app?.iconBitmap
                      AppIconItem(
                        label = displayLabel,
                        iconBitmap = displayIcon,
                        onClick = {
                          if (isEditMode) {
                            onSelectItem(item.id)
                          } else if (app != null) {
                            onLaunchApp(app)
                          } else {
                            item.packageName?.let { onLaunchPackage(it) }
                          }
                        },
                        onLongClick = {
                          if (isEditMode) onSelectItem(item.id)
                          else if (app != null) onLongClickApp(app)
                        }
                      )
                    } else if (item.type == DesktopItemType.FOLDER) {
                      val folder = folders.find { it.id == item.folderId }
                      val previewIcons = item.folderApps.mapNotNull { pkg ->
                        appsMap[pkg]?.iconBitmap
                      }.take(4)
                      AppIconItem(
                        label = item.folderName ?: "Carpeta",
                        iconBitmap = null,
                        isFolder = true,
                        folderIcons = previewIcons,
                        onClick = {
                          if (isEditMode) onSelectItem(item.id)
                          else if (folder != null) onOpenFolder(folder)
                        },
                        onLongClick = {
                          onSelectItem(item.id)
                        }
                      )
                    }

                    // Floating page/remove badges when selected in edit mode (NO ARROW BUTTONS!)
                    if (isSelected) {
                      Spacer(modifier = Modifier.height(4.dp))
                      Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                      ) {
                        Box(
                          contentAlignment = Alignment.Center,
                          modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(style.primaryHolo)
                            .clickable { onMoveToOtherPage(item.id) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                          Text(
                            text = "A Pág ${if (pageIndex == 0) 2 else 1}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF020D1D)
                          )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Box(
                          contentAlignment = Alignment.Center,
                          modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                            .clickable { onRemoveItem(item.id) }
                        ) {
                          Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Quitar",
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                          )
                        }
                      }
                    }
                  }
                }
              } else {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun DreamByteDock(
  dockApps: List<String>,
  appsMap: Map<String, AppInfo>,
  deviceType: DeviceType,
  onOpenDrawer: () -> Unit,
  onOpenSettings: () -> Unit,
  onLaunchApp: (AppInfo) -> Unit,
  onLongClickApp: (AppInfo) -> Unit,
  onLaunchPackage: (String) -> Unit
) {
  val style = LocalDreamByteStyle.current
  val maxDock = if (deviceType == DeviceType.PHONE) 4 else 6

  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 4.dp)
  ) {
    // Liquid Glass Dock Container
    Box(
      modifier = Modifier
        .widthIn(max = 680.dp)
        .fillMaxWidth()
        .clip(style.dockShape)
        .background(style.surfaceGlass)
        .border(style.borderStrokeWidth, style.borderHighlight, style.dockShape)
        .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
      Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        // Pinned dock apps
        dockApps.take(maxDock).forEach { pkg ->
          val app = appsMap[pkg]
          AppIconItem(
            label = "",
            iconBitmap = app?.iconBitmap,
            showLabel = false,
            onClick = {
              if (app != null) onLaunchApp(app) else onLaunchPackage(pkg)
            },
            onLongClick = {
              if (app != null) onLongClickApp(app)
            }
          )
        }

        // App Drawer trigger button
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(style.primaryGradient)
            .clickable(onClick = onOpenDrawer)
        ) {
          Icon(
            imageVector = Icons.Default.Apps,
            contentDescription = "Cajón de Aplicaciones",
            tint = Color(0xFF020D1D),
            modifier = Modifier.size(26.dp)
          )
        }

        // Quick Settings Shortcut
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(style.surfaceCard)
            .border(1.dp, style.borderHighlight, RoundedCornerShape(14.dp))
            .clickable(onClick = onOpenSettings)
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Ajustes DreamByte",
            tint = style.primaryHolo,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun DreamPadTaskbar(
  dockApps: List<String>,
  appsMap: Map<String, AppInfo>,
  batteryLevel: Int,
  onOpenDrawer: () -> Unit,
  onOpenSettings: () -> Unit,
  onLaunchApp: (AppInfo) -> Unit,
  onLaunchPackage: (String) -> Unit
) {
  val style = LocalDreamByteStyle.current
  val timeString = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }

  // Full-width laptop/desktop style taskbar
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(54.dp)
      .background(style.surfaceOverlay)
      .border(style.borderStrokeWidth, style.borderHighlight)
      .padding(horizontal = 14.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
    ) {
      // Start Menu / DreamByte Drawer Button
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(style.primaryHolo.copy(alpha = 0.2f))
          .border(1.dp, style.primaryHolo, RoundedCornerShape(8.dp))
          .clickable(onClick = onOpenDrawer)
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Apps,
          contentDescription = "Inicio DreamPad",
          tint = style.primaryHolo,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "DreamByte",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = style.textPrimary
        )
      }

      // Center: Pinned taskbar apps
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        dockApps.take(8).forEach { pkg ->
          val app = appsMap[pkg]
          AppIconItem(
            label = "",
            iconBitmap = app?.iconBitmap,
            showLabel = false,
            onClick = {
              if (app != null) onLaunchApp(app) else onLaunchPackage(pkg)
            }
          )
        }
      }

      // Right: System Tray (Clock, Battery, Settings)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(style.surfaceGlass)
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Bolt,
          contentDescription = null,
          tint = style.primaryHolo,
          modifier = Modifier.size(14.dp)
        )
        Text(
          text = "$batteryLevel%",
          fontSize = 11.sp,
          color = style.textPrimary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = timeString,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = style.textPrimary
        )
        Spacer(modifier = Modifier.width(6.dp))
        IconButton(
          onClick = onOpenSettings,
          modifier = Modifier.size(24.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            tint = style.textSecondary,
            modifier = Modifier.size(15.dp)
          )
        }
      }
    }
  }
}
