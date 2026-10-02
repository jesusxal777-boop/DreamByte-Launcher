package com.example.dreambyte.data

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.example.dreambyte.model.DeviceType

object DeviceDetector {

  fun detectDevice(context: Context, override: DeviceType = DeviceType.AUTO): DeviceType {
    if (override != DeviceType.AUTO) {
      return override
    }

    val config = context.resources.configuration
    val screenWidthDp = config.screenWidthDp
    val screenHeightDp = config.screenHeightDp
    val smallestWidthDp = config.smallestScreenWidthDp

    return when {
      // Laptop/Desktop mode: Wide horizontal landscape screen or DeX/Desktop connected
      screenWidthDp >= 840 || (smallestWidthDp >= 600 && screenWidthDp > screenHeightDp && screenWidthDp >= 900) -> {
        DeviceType.DREAM_PAD
      }
      // Tablet: Medium-to-expanded screen size
      smallestWidthDp >= 600 || screenWidthDp >= 600 -> {
        DeviceType.DREAM_TAB
      }
      // Standard smartphone
      else -> {
        DeviceType.PHONE
      }
    }
  }

  @Composable
  fun currentDevice(override: DeviceType = DeviceType.AUTO): DeviceType {
    if (override != DeviceType.AUTO) return override
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val smallestWidthDp = configuration.smallestScreenWidthDp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    return when {
      screenWidthDp >= 840 || (isLandscape && screenWidthDp >= 800) -> DeviceType.DREAM_PAD
      smallestWidthDp >= 600 || screenWidthDp >= 600 -> DeviceType.DREAM_TAB
      else -> DeviceType.PHONE
    }
  }
}
