package com.example.dreambyte.data

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min

object WallpaperHelper {

  // Max resolution for memory efficiency (keeps RAM usage minimal)
  private const val MAX_WALLPAPER_WIDTH = 960
  private const val MAX_WALLPAPER_HEIGHT = 1600

  fun loadSystemWallpaper(context: Context): Bitmap? {
    return try {
      val wallpaperManager = WallpaperManager.getInstance(context)
      val drawable: Drawable? = try {
        wallpaperManager.drawable ?: wallpaperManager.builtInDrawable
      } catch (_: SecurityException) {
        // Handle Android 13+ permission restrictions gracefully without crashing
        null
      } catch (_: Throwable) {
        null
      }

      if (drawable != null) {
        val srcWidth = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else MAX_WALLPAPER_WIDTH
        val srcHeight = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else MAX_WALLPAPER_HEIGHT

        // Downscale proportionally to fit within MAX dimensions
        val scale = min(
          MAX_WALLPAPER_WIDTH.toFloat() / srcWidth,
          MAX_WALLPAPER_HEIGHT.toFloat() / srcHeight
        ).coerceAtMost(1f)

        val targetWidth = (srcWidth * scale).toInt().coerceIn(320, MAX_WALLPAPER_WIDTH)
        val targetHeight = (srcHeight * scale).toInt().coerceIn(480, MAX_WALLPAPER_HEIGHT)

        // Use RGB_565 for wallpapers to cut memory consumption in HALF (prevents OOM)
        val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, targetWidth, targetHeight)
        drawable.draw(canvas)
        bitmap
      } else {
        null
      }
    } catch (_: OutOfMemoryError) {
      System.gc()
      null
    } catch (_: Throwable) {
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
    } catch (_: Throwable) {
      null
    }
  }

  fun loadCustomWallpaper(path: String?): Bitmap? {
    if (path.isNullOrBlank()) return null
    return try {
      val file = File(path)
      if (!file.exists() || !file.canRead()) return null

      // First decode dimensions only to calculate inSampleSize (prevent OOM)
      val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
      }
      BitmapFactory.decodeFile(file.absolutePath, options)

      var sampleSize = 1
      while ((options.outWidth / sampleSize) > MAX_WALLPAPER_WIDTH ||
        (options.outHeight / sampleSize) > MAX_WALLPAPER_HEIGHT
      ) {
        sampleSize *= 2
      }

      options.inJustDecodeBounds = false
      options.inSampleSize = sampleSize
      options.inPreferredConfig = Bitmap.Config.RGB_565

      BitmapFactory.decodeFile(file.absolutePath, options)
    } catch (_: OutOfMemoryError) {
      System.gc()
      null
    } catch (_: Throwable) {
      null
    }
  }
}
