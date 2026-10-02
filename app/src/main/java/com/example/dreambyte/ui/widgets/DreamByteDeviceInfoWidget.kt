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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreambyte.model.DeviceMetrics
import com.example.dreambyte.ui.components.GlassCard
import com.example.dreambyte.ui.theme.LocalDreamByteStyle

@Composable
fun DreamByteDeviceInfoWidget(
  metrics: DeviceMetrics,
  modifier: Modifier = Modifier
) {
  val style = LocalDreamByteStyle.current

  val usedRam = (metrics.totalRamMb - metrics.availableRamMb).coerceAtLeast(0L)
  val ramPercent = if (metrics.totalRamMb > 0) (usedRam.toFloat() / metrics.totalRamMb).coerceIn(0f, 1f) else 0.5f

  val usedStorage = (metrics.totalStorageGb - metrics.availableStorageGb).coerceAtLeast(0L)
  val storagePercent = if (metrics.totalStorageGb > 0) (usedStorage.toFloat() / metrics.totalStorageGb).coerceIn(0f, 1f) else 0.5f

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
            imageVector = Icons.Default.Terminal,
            contentDescription = null,
            tint = style.primaryHolo,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "DREAMBYTE SYSTEM DIAGNOSTIC",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = style.textSecondary
          )
        }

        Text(
          text = metrics.dreamByteOsVersion,
          fontSize = 10.sp,
          color = style.primaryHolo
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Device info banner
      Text(
        text = metrics.deviceModel.ifBlank { "Dispositivo Android" },
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = style.textPrimary
      )
      Text(
        text = metrics.androidVersion,
        fontSize = 11.sp,
        color = style.textMuted
      )

      Spacer(modifier = Modifier.height(12.dp))

      // RAM Meter
      MetricBar(
        title = "Memoria RAM",
        icon = Icons.Default.Memory,
        usedText = "${usedRam / 1024f}GB / ${metrics.totalRamMb / 1024f}GB",
        fraction = ramPercent,
        color = style.primaryHolo
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Storage Meter
      MetricBar(
        title = "Almacenamiento",
        icon = Icons.Default.SdStorage,
        usedText = "${usedStorage}GB / ${metrics.totalStorageGb}GB",
        fraction = storagePercent,
        color = style.primaryAccent
      )
    }
  }
}

@Composable
private fun MetricBar(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  usedText: String,
  fraction: Float,
  color: Color
) {
  val style = LocalDreamByteStyle.current
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = style.textMuted,
          modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = title,
          fontSize = 11.sp,
          color = style.textSecondary
        )
      }

      Text(
        text = usedText,
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Medium,
        color = style.textMuted
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp))
        .background(Color(0x33000000))
        .border(0.6.dp, style.borderHighlight, RoundedCornerShape(3.dp))
    ) {
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .fillMaxWidth(fraction)
          .clip(RoundedCornerShape(3.dp))
          .background(color)
      )
    }
  }
}
