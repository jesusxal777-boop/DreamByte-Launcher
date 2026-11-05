package com.example.dreambyte.ui.widgets

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreambyte.ui.components.GlassCard
import com.example.dreambyte.ui.theme.LocalDreamByteStyle

data class MediaTrack(
  val title: String,
  val artist: String,
  val duration: String
)

@Composable
fun DreamByteMediaWidget(
  modifier: Modifier = Modifier
) {
  val style = LocalDreamByteStyle.current

  val playlist = remember {
    listOf(
      MediaTrack("Cyber Neon Dreams", "DreamByte Synthetics", "3:42"),
      MediaTrack("Holographic Horizon", "Android OS Chillwave", "4:15"),
      MediaTrack("Pulse of the Void", "HyperGrid Beats", "2:58"),
      MediaTrack("Liquid Glass Echoes", "SubZero Soundscape", "3:30")
    )
  }

  var currentTrackIndex by remember { mutableIntStateOf(0) }
  var isPlaying by remember { mutableStateOf(false) }
  var progress by remember { mutableFloatStateOf(0.35f) }

  val currentTrack = playlist[currentTrackIndex]

  // Animated visualizer bars
  val infiniteTransition = rememberInfiniteTransition(label = "audio_visualizer")
  val bar1 by infiniteTransition.animateFloat(
    initialValue = 0.2f, targetValue = 0.9f,
    animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
    label = "b1"
  )
  val bar2 by infiniteTransition.animateFloat(
    initialValue = 0.7f, targetValue = 0.3f,
    animationSpec = infiniteRepeatable(tween(420, easing = LinearEasing), RepeatMode.Reverse),
    label = "b2"
  )
  val bar3 by infiniteTransition.animateFloat(
    initialValue = 0.4f, targetValue = 1f,
    animationSpec = infiniteRepeatable(tween(280, easing = LinearEasing), RepeatMode.Reverse),
    label = "b3"
  )
  val bar4 by infiniteTransition.animateFloat(
    initialValue = 0.8f, targetValue = 0.15f,
    animationSpec = infiniteRepeatable(tween(390, easing = LinearEasing), RepeatMode.Reverse),
    label = "b4"
  )

  GlassCard(modifier = modifier.fillMaxWidth()) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
      // Header: Widget Title & Visualizer icon
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = null,
            tint = style.primaryHolo,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "REPRODUCTOR MULTIMEDIA",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = style.textSecondary
          )
        }

        // Live Audio Equalizer Bars
        Row(
          horizontalArrangement = Arrangement.spacedBy(2.dp),
          verticalAlignment = Alignment.Bottom,
          modifier = Modifier.height(14.dp)
        ) {
          val activeBar1 = if (isPlaying) bar1 else 0.2f
          val activeBar2 = if (isPlaying) bar2 else 0.3f
          val activeBar3 = if (isPlaying) bar3 else 0.25f
          val activeBar4 = if (isPlaying) bar4 else 0.15f

          Box(
            modifier = Modifier
              .width(3.dp)
              .fillMaxHeight(activeBar1)
              .clip(RoundedCornerShape(2.dp))
              .background(style.primaryHolo)
          )
          Box(
            modifier = Modifier
              .width(3.dp)
              .fillMaxHeight(activeBar2)
              .clip(RoundedCornerShape(2.dp))
              .background(style.primaryAccent)
          )
          Box(
            modifier = Modifier
              .width(3.dp)
              .fillMaxHeight(activeBar3)
              .clip(RoundedCornerShape(2.dp))
              .background(style.secondaryAccent)
          )
          Box(
            modifier = Modifier
              .width(3.dp)
              .fillMaxHeight(activeBar4)
              .clip(RoundedCornerShape(2.dp))
              .background(style.primaryHolo)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Track Info & Controls
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        // Holographic Album Art Pill
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
              Brush.linearGradient(
                listOf(
                  style.primaryHolo.copy(alpha = 0.3f),
                  Color(0xFF8B5CF6).copy(alpha = 0.2f)
                )
              )
            )
            .border(1.dp, style.primaryHolo.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        ) {
          Icon(
            imageVector = Icons.Default.GraphicEq,
            contentDescription = null,
            tint = style.primaryHolo,
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title & Artist
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = currentTrack.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = style.textPrimary,
            maxLines = 1
          )
          Text(
            text = currentTrack.artist,
            fontSize = 11.sp,
            color = style.textSecondary,
            maxLines = 1
          )
        }

        // Controls
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Prev
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(30.dp)
              .clip(CircleShape)
              .background(style.surfaceGlass)
              .clickable {
                currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else playlist.size - 1
                progress = 0f
              }
          ) {
            Icon(
              imageVector = Icons.Default.FastRewind,
              contentDescription = "Anterior",
              tint = style.textPrimary,
              modifier = Modifier.size(16.dp)
            )
          }

          // Play/Pause
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(style.primaryHolo)
              .clickable { isPlaying = !isPlaying }
          ) {
            Icon(
              imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (isPlaying) "Pausar" else "Reproducir",
              tint = Color(0xFF020D1D),
              modifier = Modifier.size(22.dp)
            )
          }

          // Next
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(30.dp)
              .clip(CircleShape)
              .background(style.surfaceGlass)
              .clickable {
                currentTrackIndex = (currentTrackIndex + 1) % playlist.size
                progress = 0f
              }
          ) {
            Icon(
              imageVector = Icons.Default.FastForward,
              contentDescription = "Siguiente",
              tint = style.textPrimary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Track Progress Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(5.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(Color(0x33000000))
          .border(0.5.dp, style.borderHighlight, RoundedCornerShape(3.dp))
      ) {
        Box(
          modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(progress)
            .clip(RoundedCornerShape(3.dp))
            .background(
              Brush.horizontalGradient(
                listOf(style.primaryHolo, style.primaryAccent)
              )
            )
        )
      }
    }
  }
}
