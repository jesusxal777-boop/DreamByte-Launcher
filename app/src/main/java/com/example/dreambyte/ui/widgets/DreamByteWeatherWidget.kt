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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreambyte.ui.components.GlassCard
import com.example.dreambyte.ui.theme.LocalDreamByteStyle

data class ForecastDay(
  val day: String,
  val temp: String,
  val icon: ImageVector,
  val status: String
)

@Composable
fun DreamByteWeatherWidget(
  modifier: Modifier = Modifier,
  onRefresh: () -> Unit = {}
) {
  val style = LocalDreamByteStyle.current
  var isFahrenheit by remember { mutableStateOf(false) }
  var isRefreshing by remember { mutableStateOf(false) }

  val infiniteTransition = rememberInfiniteTransition(label = "weather_sun_rotation")
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(20000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "sun_spin"
  )

  val tempC = 23
  val tempF = 73
  val currentTemp = if (isFahrenheit) "$tempF°F" else "$tempC°C"
  val minMax = if (isFahrenheit) "Max 77° · Min 62°" else "Max 25° · Min 17°"

  val forecast = listOf(
    ForecastDay("Hoy", if (isFahrenheit) "73°" else "23°", Icons.Default.WbSunny, "Despejado"),
    ForecastDay("Mañana", if (isFahrenheit) "69°" else "21°", Icons.Default.Cloud, "Nublado"),
    ForecastDay("Jue", if (isFahrenheit) "66°" else "19°", Icons.Default.Thunderstorm, "Lluvia"),
    ForecastDay("Vie", if (isFahrenheit) "71°" else "22°", Icons.Default.WbSunny, "Soleado")
  )

  GlassCard(modifier = modifier.fillMaxWidth()) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
      // Header: Location & Temperature toggle
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "CLIMA EN TIEMPO REAL",
              fontSize = 10.5.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              color = style.textSecondary
            )
          }
          Text(
            text = "Estación Satelital OS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = style.textPrimary
          )
        }

        // Unit switcher & Refresh
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(style.surfaceGlass)
              .border(0.6.dp, style.borderHighlight, RoundedCornerShape(8.dp))
              .clickable { isFahrenheit = !isFahrenheit }
              .padding(horizontal = 7.dp, vertical = 3.dp)
          ) {
            Text(
              text = if (isFahrenheit) "°F" else "°C",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = style.primaryHolo
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .clickable {
                isRefreshing = true
                onRefresh()
              }
              .padding(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Actualizar clima",
              tint = style.textSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Current Conditions Main Row
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(54.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(
                Brush.linearGradient(
                  listOf(
                    style.primaryHolo.copy(alpha = 0.25f),
                    style.primaryAccent.copy(alpha = 0.1f)
                  )
                )
              )
              .border(1.dp, style.primaryHolo.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
          ) {
            Icon(
              imageVector = Icons.Default.WbSunny,
              contentDescription = "Soleado",
              tint = style.primaryHolo,
              modifier = Modifier
                .size(34.dp)
                .rotate(rotation)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = currentTemp,
              fontSize = 28.sp,
              fontWeight = FontWeight.Black,
              color = style.textPrimary,
              letterSpacing = (-0.5).sp
            )
            Text(
              text = "Cielo Despejado · $minMax",
              fontSize = 11.sp,
              color = style.textMuted
            )
          }
        }

        // Metrics pills (Humedad & Viento)
        Column(horizontalAlignment = Alignment.End) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.WaterDrop,
              contentDescription = null,
              tint = style.primaryHolo,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "48% Humedad",
              fontSize = 10.5.sp,
              color = style.textSecondary
            )
          }
          Spacer(modifier = Modifier.height(3.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Air,
              contentDescription = null,
              tint = style.primaryAccent,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "12 km/h Viento",
              fontSize = 10.5.sp,
              color = style.textSecondary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4-Day Forecast Strip
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(style.surfaceGlass.copy(alpha = 0.4f))
          .padding(horizontal = 8.dp, vertical = 6.dp)
      ) {
        forecast.forEach { item ->
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
          ) {
            Text(
              text = item.day,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = style.textSecondary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Icon(
              imageVector = item.icon,
              contentDescription = item.status,
              tint = if (item.day == "Hoy") style.primaryHolo else style.textPrimary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = item.temp,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = style.textPrimary
            )
          }
        }
      }
    }
  }
}
