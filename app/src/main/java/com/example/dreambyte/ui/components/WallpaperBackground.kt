package com.example.dreambyte.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.dreambyte.data.WallpaperHelper
import com.example.dreambyte.model.WallpaperType
import com.example.dreambyte.ui.theme.LocalDreamByteStyle
import kotlinx.coroutines.launch
import kotlin.random.Random

data class TouchRipple(
  val id: Long,
  val center: Offset,
  val radius: Animatable<Float, *>,
  val alpha: Animatable<Float, *>,
  val color: Color
)

@Composable
fun WallpaperBackground(
  wallpaperType: WallpaperType,
  customWallpaperPath: String? = null,
  interactiveParticles: Boolean = true,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val style = LocalDreamByteStyle.current
  val scope = rememberCoroutineScope()
  val ripples = remember { mutableStateListOf<TouchRipple>() }

  // Load real system wallpaper bitmap if SYSTEM_DEFAULT is chosen
  val systemWallpaperBitmap = remember(wallpaperType) {
    if (wallpaperType == WallpaperType.SYSTEM_DEFAULT) {
      WallpaperHelper.loadSystemWallpaper(context)
    } else null
  }

  // Load custom gallery wallpaper bitmap if CUSTOM is chosen
  val customWallpaperBitmap = remember(wallpaperType, customWallpaperPath) {
    if (wallpaperType == WallpaperType.CUSTOM) {
      WallpaperHelper.loadCustomWallpaper(customWallpaperPath)
    } else null
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .pointerInput(interactiveParticles) {
        if (interactiveParticles) {
          detectTapGestures { offset ->
            val id = System.currentTimeMillis() + Random.nextLong(1000)
            val radius = Animatable(0f)
            val alpha = Animatable(0.85f)
            val rippleColor = if (Random.nextBoolean()) style.primaryHolo else style.primaryAccent

            val ripple = TouchRipple(
              id = id,
              center = offset,
              radius = radius,
              alpha = alpha,
              color = rippleColor
            )
            ripples.add(ripple)

            scope.launch {
              launch {
                radius.animateTo(
                  targetValue = 260f,
                  animationSpec = tween(durationMillis = 750)
                )
              }
              launch {
                alpha.animateTo(
                  targetValue = 0f,
                  animationSpec = tween(durationMillis = 750)
                )
              }
              ripples.remove(ripple)
            }
          }
        }
      }
  ) {
    // 1. Base Wallpaper Image / Render Layer
    when (wallpaperType) {
      WallpaperType.MODERN_HOLO -> {
        Image(
          painter = painterResource(id = R.drawable.img_wallpaper_modern),
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        // High-tech subtle dark glass gradient overlay for icon contrast
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0x35030712))
        )
      }

      WallpaperType.CYBER_GRID -> {
        // Real Holo Cyber Grid Wallpaper
        Image(
          painter = painterResource(id = R.drawable.img_wallpaper_holo),
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0x28000000))
        )
      }

      WallpaperType.CLASSIC_AURORA -> {
        // Real Classic Retro Nebula Wallpaper
        Image(
          painter = painterResource(id = R.drawable.img_wallpaper_classic),
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0x20000000))
        )
      }

      WallpaperType.DEEP_VOID -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                listOf(
                  Color(0xFF070F1E),
                  Color(0xFF030710),
                  Color(0xFF010307)
                )
              )
            )
        )
      }

      WallpaperType.SYSTEM_DEFAULT -> {
        if (systemWallpaperBitmap != null) {
          Image(
            bitmap = systemWallpaperBitmap.asImageBitmap(),
            contentDescription = "Fondo de Pantalla del Sistema",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
          // Soft tint for icon readability
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color(0x20000000))
          )
        } else {
          // Fallback if system wallpaper is inaccessible
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  listOf(
                    style.backgroundDark,
                    style.backgroundDark.copy(alpha = 0.95f)
                  )
                )
              )
          )
        }
      }

      WallpaperType.CUSTOM -> {
        if (customWallpaperBitmap != null) {
          Image(
            bitmap = customWallpaperBitmap.asImageBitmap(),
            contentDescription = "Fondo Personalizado",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color(0x25000000))
          )
        } else {
          // If no custom image selected yet, fallback to Modern
          Image(
            painter = painterResource(id = R.drawable.img_wallpaper_modern),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }

    // 2. INNOVATIVE FEATURE: Interactive Holographic Quantum Touch Ripples
    if (interactiveParticles && ripples.isNotEmpty()) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        ripples.forEach { ripple ->
          val rad = ripple.radius.value
          val alf = ripple.alpha.value
          if (alf > 0f) {
            // Expanding neon ring
            drawCircle(
              color = ripple.color.copy(alpha = alf),
              center = ripple.center,
              radius = rad,
              style = Stroke(width = 3.dp.toPx())
            )
            // Soft inner glow
            drawCircle(
              brush = Brush.radialGradient(
                colors = listOf(
                  ripple.color.copy(alpha = alf * 0.4f),
                  Color.Transparent
                ),
                center = ripple.center,
                radius = rad * 0.8f
              ),
              center = ripple.center,
              radius = rad * 0.8f
            )
          }
        }
      }
    }
  }
}
