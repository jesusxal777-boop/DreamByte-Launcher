package com.example.dreambyte.model

data class LauncherSettings(
  val appearance: DreamByteAppearance = DreamByteAppearance.MODERN,
  val wallpaperType: WallpaperType = WallpaperType.MODERN_HOLO,
  val wallpaperBlur: Float = 0f,
  val gridRows: Int = 5,
  val gridCols: Int = 4,
  val iconSize: IconSize = IconSize.NORMAL,
  val animationsEnabled: Boolean = true,
  val showDreamBot: Boolean = true,
  val deviceModeOverride: DeviceType = DeviceType.AUTO,
  val drawerLayout: DrawerLayoutMode = DrawerLayoutMode.GRID,
  val dockItemCount: Int = 5,
  val customWallpaperPath: String? = null,
  val interactiveParticlesEnabled: Boolean = true,
  val firstLaunchCompleted: Boolean = false
)
