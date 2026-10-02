package com.example.dreambyte.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreambyte.model.DreamByteAppearance
import com.example.dreambyte.ui.components.GlassCard
import com.example.dreambyte.ui.theme.LocalDreamByteStyle
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DreamByteClockWidget(
  modifier: Modifier = Modifier,
  onOpenClockApp: (() -> Unit)? = null
) {
  val style = LocalDreamByteStyle.current
  var currentTime by remember { mutableStateOf(Date()) }

  LaunchedEffect(Unit) {
    while (true) {
      currentTime = Date()
      delay(1000L)
    }
  }

  val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
  val secondsFormat = SimpleDateFormat("ss", Locale.getDefault())
  val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))

  val timeString = timeFormat.format(currentTime)
  val secondsString = secondsFormat.format(currentTime)
  val dateString = dateFormat.format(currentTime).replaceFirstChar { it.uppercase() }

  GlassCard(
    modifier = modifier.fillMaxWidth(),
    onClick = onOpenClockApp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
      // Top status row
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(style.primaryHolo)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "DREAMBYTE CORE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = style.textSecondary
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(style.primaryHolo.copy(alpha = 0.15f))
            .border(0.8.dp, style.primaryHolo.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "OS ONLINE",
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = style.primaryHolo
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Main clock display
      Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
      ) {
        Text(
          text = timeString,
          fontSize = if (style.appearance == DreamByteAppearance.HOLO) 46.sp else 50.sp,
          fontWeight = FontWeight.Black,
          fontFamily = if (style.appearance == DreamByteAppearance.HOLO) FontFamily.Monospace else FontFamily.Default,
          letterSpacing = (-1.5).sp,
          color = style.textPrimary
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
          text = ":$secondsString",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          color = style.primaryHolo,
          modifier = Modifier.padding(bottom = 8.dp)
        )
      }

      // Date string
      Text(
        text = dateString,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = style.textSecondary
      )
    }
  }
}
