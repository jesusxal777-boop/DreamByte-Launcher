package com.example.dreambyte.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import com.example.dreambyte.ui.components.AppContextMenu
import com.example.dreambyte.ui.components.AppIconItem
import com.example.dreambyte.ui.components.DreamBotAvatar
import com.example.dreambyte.ui.components.DreamBotCompanionCard
import com.example.dreambyte.ui.components.DreamByteHudSheet
import com.example.dreambyte.ui.components.FolderDialog
import com.example.dreambyte.ui.components.GlassCard
import com.example.dreambyte.ui.components.WallpaperBackground
import com.example.dreambyte.ui.theme.LocalDreamByteStyle
import com.example.dreambyte.ui.widgets.DreamByteBatteryWidget
import com.example.dreambyte.ui.widgets.DreamByteClockWidget
import com.example.dreambyte.ui.widgets.DreamByteDeviceInfoWidget
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

  val detectedDevice = DeviceDetector.currentDevice(settings.deviceModeOverride)

  LaunchedEffect(Unit) {
    viewModel.checkDefaultLauncherStatus(context)
  }

  // Pager for desktop pages (pages 0 and 1)
  val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

  Box(
    modifier = Modifier
      .fillMaxSize()
      .pointerInput(Unit) {
        detectVerticalDragGestures { _, dragAmount ->
          // Swipe up opens app drawer; swipe down opens Holographic HUD Command Center
          if (dragAmount < -20f) {
            viewModel.openDrawer()
          } else if (dragAmount > 25f) {
            viewModel.openHud()
          }
        }
      }
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
        onOpenDrawer = { viewModel.openDrawer() },
        onOpenHud = { viewModel.openHud() },
        onOpenSettings = { viewModel.openSettings() }
      )

      // Edit Mode Active Banner
      AnimatedVisibility(visible = isEditMode) {
        EditModeTopBanner(
          selectedItem = desktopItems.find { it.id == selectedMoveItemId },
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
            installedApps = installedApps,
            folders = folders,
            metrics = deviceMetrics,
            showDreamBot = settings.showDreamBot,
            deviceType = detectedDevice,
            isEditMode = isEditMode,
            selectedMoveItemId = selectedMoveItemId,
            onSelectItem = { itemId ->
              if (selectedMoveItemId != null && selectedMoveItemId != itemId) {
                // If an item was already selected and user taps another, swap them!
                viewModel.swapItems(selectedMoveItemId!!, itemId)
                viewModel.selectItemToMove(null)
              } else {
                viewModel.selectItemToMove(itemId)
              }
            },
            onMoveLeft = { itemId -> viewModel.moveItemLeft(itemId) },
            onMoveRight = { itemId -> viewModel.moveItemRight(itemId) },
            onMoveToOtherPage = { itemId ->
              val target = if (page == 0) 1 else 0
              viewModel.moveItemToPage(itemId, target)
              coroutineScope.launch { pagerState.animateScrollToPage(target) }
            },
            onRemoveItem = { itemId -> viewModel.removeDesktopItem(itemId) },
            onLaunchApp = { app ->
              if (isEditMode) {
                val item = desktopItems.find { it.packageName == app.packageName }
                if (item != null) viewModel.selectItemToMove(item.id)
              } else {
                viewModel.launchApp(context, app)
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
                if (item != null) viewModel.selectItemToMove(item.id)
              } else {
                viewModel.openFolder(folder)
              }
            },
            onOpenSettings = { viewModel.openSettings() }
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
          installedApps = installedApps,
          batteryLevel = deviceMetrics.batteryLevel,
          onOpenDrawer = { viewModel.openDrawer() },
          onOpenSettings = { viewModel.openSettings() },
          onLaunchApp = { app -> viewModel.launchApp(context, app) }
        )
      } else {
        DreamByteDock(
          dockApps = dockApps,
          installedApps = installedApps,
          deviceType = detectedDevice,
          onOpenDrawer = { viewModel.openDrawer() },
          onOpenSettings = { viewModel.openSettings() },
          onLaunchApp = { app -> viewModel.launchApp(context, app) },
          onLongClickApp = { app -> viewModel.openAppContext(app) }
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
        installedApps.find { it.packageName == pkg }
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

    // Right: Action Buttons (Organizar, Cajón de Apps, HUD, Batería, Settings)
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

      Spacer(modifier = Modifier.width(5.dp))

      // HUD / Command Center quick button
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(10.dp))
          .background(style.surfaceGlass)
          .border(0.8.dp, style.borderHighlight, RoundedCornerShape(10.dp))
          .clickable(onClick = onOpenHud)
          .padding(horizontal = 7.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = "HUD",
            tint = style.primaryHolo,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "HUD",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = style.textPrimary
          )
        }
      }

      Spacer(modifier = Modifier.width(5.dp))

      // Drawer quick button
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(10.dp))
          .background(style.surfaceGlass)
          .border(0.8.dp, style.borderHighlight, RoundedCornerShape(10.dp))
          .clickable(onClick = onOpenDrawer)
          .padding(horizontal = 7.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Apps,
            contentDescription = "Cajón",
            tint = style.primaryHolo,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "Apps",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = style.textPrimary
          )
        }
      }

      Spacer(modifier = Modifier.width(5.dp))

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
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = if (selectedItem != null) "Moviendo: ${selectedItem.label.ifBlank { selectedItem.folderName ?: "Elemento" }}" else "Modo Organizar Activo",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = style.primaryHolo
        )
        Text(
          text = if (selectedItem != null) "Usa las flechas del icono o toca otro para intercambiar" else "Toca una app o carpeta para moverla o cambiarla de página",
          fontSize = 10.5.sp,
          color = style.textPrimary
        )
      }

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
}

@Composable
private fun DesktopPageContent(
  pageIndex: Int,
  desktopItems: List<DesktopItem>,
  installedApps: List<AppInfo>,
  folders: List<FolderInfo>,
  metrics: com.example.dreambyte.model.DeviceMetrics,
  showDreamBot: Boolean,
  deviceType: DeviceType,
  isEditMode: Boolean,
  selectedMoveItemId: String?,
  onSelectItem: (String) -> Unit,
  onMoveLeft: (String) -> Unit,
  onMoveRight: (String) -> Unit,
  onMoveToOtherPage: (String) -> Unit,
  onRemoveItem: (String) -> Unit,
  onLaunchApp: (AppInfo) -> Unit,
  onLongClickApp: (AppInfo) -> Unit,
  onOpenFolder: (FolderInfo) -> Unit,
  onOpenSettings: () -> Unit
) {
  val style = LocalDreamByteStyle.current

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp, vertical = 4.dp)
  ) {
    if (pageIndex == 0) {
      // Clock Widget
      DreamByteClockWidget(onOpenClockApp = onOpenSettings)
      Spacer(modifier = Modifier.height(8.dp))

      // DreamBot Companion Mascot Card
      if (showDreamBot) {
        DreamBotCompanionCard(onOpenSettings = onOpenSettings)
        Spacer(modifier = Modifier.height(8.dp))
      }

      // App shortcuts & folders on page 0
      val appItems = desktopItems.filter { it.type == DesktopItemType.APP || it.type == DesktopItemType.FOLDER }
      if (appItems.isNotEmpty()) {
        val cols = if (deviceType == DeviceType.PHONE) 4 else 6
        val rows = (appItems.size + cols - 1) / cols

        Column(
          verticalArrangement = Arrangement.spacedBy(8.dp),
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
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                              if (isSelected) style.primaryHolo.copy(alpha = 0.25f)
                              else style.surfaceGlass.copy(alpha = 0.4f)
                            )
                            .border(
                              if (isSelected) 1.5.dp else 0.8.dp,
                              if (isSelected) style.primaryHolo else style.borderHighlight.copy(alpha = 0.4f),
                              RoundedCornerShape(12.dp)
                            )
                        } else Modifier
                      )
                      .padding(vertical = 2.dp)
                  ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      if (item.type == DesktopItemType.APP) {
                        val app = installedApps.find { it.packageName == item.packageName }
                        if (app != null) {
                          AppIconItem(
                            label = app.label,
                            iconBitmap = app.iconBitmap,
                            onClick = {
                              if (isEditMode) onSelectItem(item.id) else onLaunchApp(app)
                            },
                            onLongClick = {
                              if (isEditMode) onSelectItem(item.id) else onLongClickApp(app)
                            }
                          )
                        }
                      } else if (item.type == DesktopItemType.FOLDER) {
                        val folder = folders.find { it.id == item.folderId }
                        val previewIcons = item.folderApps.mapNotNull { pkg ->
                          installedApps.find { it.packageName == pkg }?.iconBitmap
                        }.take(4)
                        AppIconItem(
                          label = item.folderName ?: "Carpeta",
                          iconBitmap = null,
                          isFolder = true,
                          folderIcons = previewIcons,
                          onClick = {
                            if (isEditMode) onSelectItem(item.id) else if (folder != null) onOpenFolder(folder)
                          },
                          onLongClick = {
                            onSelectItem(item.id)
                          }
                        )
                      }

                      // Edit mode movement quick buttons when this item is selected
                      if (isSelected) {
                        Row(
                          horizontalArrangement = Arrangement.Center,
                          verticalAlignment = Alignment.CenterVertically,
                          modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp)
                        ) {
                          Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                              .size(22.dp)
                              .clip(CircleShape)
                              .background(style.primaryHolo)
                              .clickable { onMoveLeft(item.id) }
                          ) {
                            Icon(
                              imageVector = Icons.Default.ArrowBack,
                              contentDescription = "Mover izquierda",
                              tint = Color(0xFF020D1D),
                              modifier = Modifier.size(14.dp)
                            )
                          }
                          Spacer(modifier = Modifier.width(4.dp))
                          Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                              .size(22.dp)
                              .clip(CircleShape)
                              .background(style.primaryHolo)
                              .clickable { onMoveRight(item.id) }
                          ) {
                            Icon(
                              imageVector = Icons.Default.ArrowForward,
                              contentDescription = "Mover derecha",
                              tint = Color(0xFF020D1D),
                              modifier = Modifier.size(14.dp)
                            )
                          }
                          Spacer(modifier = Modifier.width(4.dp))
                          Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                              .size(22.dp)
                              .clip(CircleShape)
                              .background(style.primaryAccent)
                              .clickable { onMoveToOtherPage(item.id) }
                          ) {
                            Icon(
                              imageVector = Icons.Default.SwapHoriz,
                              contentDescription = "Mover a página 2",
                              tint = Color(0xFF020D1D),
                              modifier = Modifier.size(14.dp)
                            )
                          }
                          Spacer(modifier = Modifier.width(4.dp))
                          Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                              .size(22.dp)
                              .clip(CircleShape)
                              .background(Color(0xFFEF4444))
                              .clickable { onRemoveItem(item.id) }
                          ) {
                            Icon(
                              imageVector = Icons.Default.Close,
                              contentDescription = "Quitar",
                              tint = Color.White,
                              modifier = Modifier.size(13.dp)
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
    } else {
      // Page 1: Hardware & System Diagnostic Widgets + More Apps
      DreamByteBatteryWidget(metrics = metrics)
      Spacer(modifier = Modifier.height(8.dp))
      DreamByteDeviceInfoWidget(metrics = metrics)
      Spacer(modifier = Modifier.height(8.dp))

      // Extra apps on page 1 with reordering support
      val page1Items = desktopItems.filter { it.page == 1 && (it.type == DesktopItemType.APP || it.type == DesktopItemType.FOLDER) }
      val displayItems = if (page1Items.isNotEmpty()) page1Items else {
        // Fallback to extra installed apps
        installedApps.drop(8).take(8).map { app ->
          DesktopItem(
            id = app.packageName,
            type = DesktopItemType.APP,
            page = 1,
            packageName = app.packageName,
            label = app.label
          )
        }
      }

      val cols = if (deviceType == DeviceType.PHONE) 4 else 6
      Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
      ) {
        displayItems.take(cols).forEach { item ->
          val app = installedApps.find { it.packageName == item.packageName }
          if (app != null) {
            val isSelected = isEditMode && selectedMoveItemId == item.id
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .weight(1f)
                .then(
                  if (isSelected) {
                    Modifier
                      .border(1.dp, style.primaryHolo, RoundedCornerShape(12.dp))
                      .padding(2.dp)
                  } else Modifier
                )
            ) {
              AppIconItem(
                label = app.label,
                iconBitmap = app.iconBitmap,
                onClick = {
                  if (isEditMode) onSelectItem(item.id) else onLaunchApp(app)
                },
                onLongClick = {
                  if (isEditMode) onSelectItem(item.id) else onLongClickApp(app)
                }
              )
              if (isSelected) {
                Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(style.primaryHolo)
                    .clickable { onMoveToOtherPage(item.id) }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text("A Pág 1", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF020D1D))
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun DreamByteDock(
  dockApps: List<String>,
  installedApps: List<AppInfo>,
  deviceType: DeviceType,
  onOpenDrawer: () -> Unit,
  onOpenSettings: () -> Unit,
  onLaunchApp: (AppInfo) -> Unit,
  onLongClickApp: (AppInfo) -> Unit
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
          val app = installedApps.find { it.packageName == pkg }
          if (app != null) {
            AppIconItem(
              label = "",
              iconBitmap = app.iconBitmap,
              showLabel = false,
              onClick = { onLaunchApp(app) },
              onLongClick = { onLongClickApp(app) }
            )
          }
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
  installedApps: List<AppInfo>,
  batteryLevel: Int,
  onOpenDrawer: () -> Unit,
  onOpenSettings: () -> Unit,
  onLaunchApp: (AppInfo) -> Unit
) {
  val style = LocalDreamByteStyle.current
  val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

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
          val app = installedApps.find { it.packageName == pkg }
          if (app != null) {
            AppIconItem(
              label = "",
              iconBitmap = app.iconBitmap,
              showLabel = false,
              onClick = { onLaunchApp(app) }
            )
          }
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
