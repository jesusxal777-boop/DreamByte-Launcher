package com.example.dreambyte.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PhoneMcpOverlayManager {

  private val _isOverlayActive = MutableStateFlow(false)
  val isOverlayActive: StateFlow<Boolean> = _isOverlayActive.asStateFlow()

  fun canDrawOverlays(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Settings.canDrawOverlays(context)
    } else {
      true
    }
  }

  fun requestOverlayPermission(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (!Settings.canDrawOverlays(activity)) {
        try {
          val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${activity.packageName}")
          )
          activity.startActivity(intent)
          Toast.makeText(
            activity,
            "Activa 'Permitir superposición sobre otras apps' para PhoneMCP",
            Toast.LENGTH_LONG
          ).show()
        } catch (_: Exception) {
          try {
            activity.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
          } catch (_: Exception) {
            Toast.makeText(activity, "Abre Configuración -> Apps -> Superposición", Toast.LENGTH_SHORT).show()
          }
        }
      }
    }
  }

  fun startOverlay(context: Context): Boolean {
    if (!canDrawOverlays(context)) {
      Toast.makeText(context, "Requiere permiso de superposición (Overlay)", Toast.LENGTH_SHORT).show()
      return false
    }

    val intent = Intent(context, PhoneMcpOverlayService::class.java).apply {
      action = PhoneMcpOverlayService.ACTION_START
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      context.startForegroundService(intent)
    } else {
      context.startService(intent)
    }
    _isOverlayActive.value = true
    return true
  }

  fun stopOverlay(context: Context) {
    val intent = Intent(context, PhoneMcpOverlayService::class.java).apply {
      action = PhoneMcpOverlayService.ACTION_STOP
    }
    context.startService(intent)
    _isOverlayActive.value = false
  }

  fun movePointer(context: Context, x: Int, y: Int) {
    val intent = Intent(context, PhoneMcpOverlayService::class.java).apply {
      action = PhoneMcpOverlayService.ACTION_MOVE_POINTER
      putExtra(PhoneMcpOverlayService.EXTRA_X, x)
      putExtra(PhoneMcpOverlayService.EXTRA_Y, y)
    }
    context.startService(intent)
  }

  fun triggerClick(context: Context, x: Int, y: Int) {
    val intent = Intent(context, PhoneMcpOverlayService::class.java).apply {
      action = PhoneMcpOverlayService.ACTION_TRIGGER_CLICK
      putExtra(PhoneMcpOverlayService.EXTRA_X, x)
      putExtra(PhoneMcpOverlayService.EXTRA_Y, y)
    }
    context.startService(intent)
  }

  fun isRunning(): Boolean {
    return PhoneMcpOverlayService.isServiceRunning
  }
}
