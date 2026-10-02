package com.example.dreambyte.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreambyte.model.DeviceMetrics
import com.example.dreambyte.ui.components.GlassCard
import com.example.dreambyte.ui.theme.LocalDreamByteStyle

@Composable
fun DreamByteBatteryWidget(
  metrics: DeviceMetrics,
  modifier: Modifier = Modifier
) {
  val style = LocalDreamByteStyle.current
  val level = metrics.batteryLevel.coerceIn(0, 100)

  val barColor = when {
    metrics.isCharging -> style.primaryHolo
    level > 20 -> style.primaryAccent
    else -> Color(0xFFEF4444)
  }

  GlassCard(modifier = modifier.fillMaxWidth()) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = null,
            tint = if (metrics.isCharging) style.primaryHolo else style.textSecondary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "BATERÍA DREAMBYTE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = style.textSecondary
          )
        }

        Text(
          text = "$level%",
          fontSize = 16.sp,
          fontWeight = FontWeight.Black,
          color = if (metrics.isCharging) style.primaryHolo else style.textPrimary
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Battery Progress Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(10.dp)
          .clip(RoundedCornerShape(5.dp))
          .background(Color(0x33000000))
          .border(0.8.dp, style.borderHighlight, RoundedCornerShape(5.dp))
      ) {
        Box(
          modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(level / 100f)
            .clip(RoundedCornerShape(5.dp))
            .background(
              Brush.horizontalGradient(
                listOf(style.secondaryAccent, barColor)
              )
            )
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = metrics.batteryStatus,
          fontSize = 11.sp,
          color = style.textMuted
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(if (metrics.isCharging) Color(0xFF10B981) else style.textMuted)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (metrics.isCharging) "Carga Rápida Activa" else "Consumo Normal",
            fontSize = 10.5.sp,
            color = style.textMuted
          )
        }
      }
    }
  }
}
