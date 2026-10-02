package com.example.dreambyte.model

import android.graphics.Bitmap

data class AppInfo(
  val packageName: String,
  val activityName: String,
  val label: String,
  val iconBitmap: Bitmap? = null,
  val isSystemApp: Boolean = false,
  val installTime: Long = 0L
)

enum class DesktopItemType {
  APP,
  FOLDER,
  WIDGET
}

data class DesktopItem(
  val id: String,
  val type: DesktopItemType,
  val page: Int = 0,
  val cellX: Int = 0,
  val cellY: Int = 0,
  val spanX: Int = 1,
  val spanY: Int = 1,
  // If APP
  val packageName: String? = null,
  val activityName: String? = null,
  val label: String = "",
  // If FOLDER
  val folderId: String? = null,
  val folderName: String? = null,
  val folderApps: List<String> = emptyList(), // list of packageNames
  // If WIDGET
  val widgetType: WidgetType? = null
)

data class FolderInfo(
  val id: String,
  val name: String,
  val packageNames: List<String>
)

data class DeviceMetrics(
  val totalRamMb: Long = 0L,
  val availableRamMb: Long = 0L,
  val totalStorageGb: Long = 0L,
  val availableStorageGb: Long = 0L,
  val batteryLevel: Int = 100,
  val isCharging: Boolean = false,
  val batteryStatus: String = "Descargando",
  val deviceModel: String = "",
  val androidVersion: String = "",
  val dreamByteOsVersion: String = "DreamByte OS v1.0.4 Alpha"
)
