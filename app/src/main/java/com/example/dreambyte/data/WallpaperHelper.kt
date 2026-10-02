package com.example.dreambyte.data

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object WallpaperHelper {

  fun loadSystemWallpaper(context: Context): Bitmap? {
    return try {
      val wallpaperManager = WallpaperManager.getInstance(context)
      val drawable: Drawable? = wallpaperManager.drawable ?: wallpaperManager.builtInDrawable
      if (drawable != null) {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
          return drawable.bitmap
        }
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 1080
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bitmap
      } else {
        null
      }
    } catch (_: Exception) {
      null
    }
  }

  fun saveCustomWallpaperFromUri(context: Context, uri: Uri): String? {
    return try {
      val inputStream = context.contentResolver.openInputStream(uri) ?: return null
      val file = File(context.filesDir, "custom_wallpaper.jpg")
      val outputStream = FileOutputStream(file)
      inputStream.use { input ->
        outputStream.use { output ->
          input.copyTo(output)
        }
      }
      file.absolutePath
    } catch (_: Exception) {
      null
    }
  }

  fun loadCustomWallpaper(path: String?): Bitmap? {
    if (path.isNullOrBlank()) return null
    return try {
      val file = File(path)
      if (file.exists()) {
        BitmapFactory.decodeFile(file.absolutePath)
      } else {
        null
      }
    } catch (_: Exception) {
      null
    }
  }
}
