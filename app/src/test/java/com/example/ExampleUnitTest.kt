package com.example

import com.example.dreambyte.model.DeviceType
import com.example.dreambyte.model.DreamByteAppearance
import com.example.dreambyte.model.IconSize
import com.example.dreambyte.model.LauncherSettings
import com.example.dreambyte.model.WallpaperType
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun launcherSettings_defaultValues_areCorrect() {
    val settings = LauncherSettings()
    assertEquals(DreamByteAppearance.MODERN, settings.appearance)
    assertEquals(WallpaperType.MODERN_HOLO, settings.wallpaperType)
    assertEquals(5, settings.gridRows)
    assertEquals(4, settings.gridCols)
    assertEquals(IconSize.NORMAL, settings.iconSize)
    assertTrue(settings.animationsEnabled)
    assertTrue(settings.showDreamBot)
    assertEquals(DeviceType.AUTO, settings.deviceModeOverride)
  }

  @Test
  fun dreamByteAppearance_entries_haveExpectedTitles() {
    assertEquals("DreamByte Modern", DreamByteAppearance.MODERN.title)
    assertEquals("DreamByte Holo", DreamByteAppearance.HOLO.title)
    assertEquals("DreamByte Classic", DreamByteAppearance.CLASSIC.title)
  }

  @Test
  fun wallpaperType_entries_includeSystemAndCustom() {
    assertTrue(WallpaperType.entries.contains(WallpaperType.SYSTEM_DEFAULT))
    assertTrue(WallpaperType.entries.contains(WallpaperType.CUSTOM))
    assertEquals("Fondo del Sistema", WallpaperType.SYSTEM_DEFAULT.displayName)
  }

  @Test
  fun widgetType_entries_includeAllRealWidgets() {
    val types = com.example.dreambyte.model.WidgetType.entries
    assertTrue(types.contains(com.example.dreambyte.model.WidgetType.CLOCK))
    assertTrue(types.contains(com.example.dreambyte.model.WidgetType.WEATHER))
    assertTrue(types.contains(com.example.dreambyte.model.WidgetType.BATTERY))
    assertTrue(types.contains(com.example.dreambyte.model.WidgetType.DEVICE_INFO))
    assertTrue(types.contains(com.example.dreambyte.model.WidgetType.MEDIA_PLAYER))
    assertTrue(types.contains(com.example.dreambyte.model.WidgetType.QUICK_NOTES))
  }

  @Test
  fun phoneMcpOverlay_constantsAndState_areCorrect() {
    assertEquals("com.example.dreambyte.ACTION_START_PHONEMCP_OVERLAY", com.example.dreambyte.service.PhoneMcpOverlayService.ACTION_START)
    assertEquals("com.example.dreambyte.ACTION_STOP_PHONEMCP_OVERLAY", com.example.dreambyte.service.PhoneMcpOverlayService.ACTION_STOP)
    assertEquals("com.example.dreambyte.ACTION_MOVE_POINTER", com.example.dreambyte.service.PhoneMcpOverlayService.ACTION_MOVE_POINTER)
    assertEquals("com.example.dreambyte.ACTION_TRIGGER_CLICK", com.example.dreambyte.service.PhoneMcpOverlayService.ACTION_TRIGGER_CLICK)
    assertFalse(com.example.dreambyte.service.PhoneMcpOverlayService.isServiceRunning)
  }
}

