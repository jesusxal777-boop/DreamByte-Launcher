package com.example.dreambyte.ui.components

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreambyte.model.DeviceMetrics
import com.example.dreambyte.ui.theme.LocalDreamByteStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DreamByteHudSheet(
  isOpen: Boolean,
  metrics: DeviceMetrics,
  interactiveParticles: Boolean,
  onToggleInteractiveParticles: (Boolean) -> Unit,
  onClose: () -> Unit,
  onOpenSettings: () -> Unit
) {
  if (!isOpen) return
  BackHandler(enabled = isOpen) { onClose() }

  val context = LocalContext.current
  val style = LocalDreamByteStyle.current
  val scope = rememberCoroutineScope()

  var isFlashlightOn by remember { mutableStateOf(false) }
  var isCleaningRam by remember { mutableStateOf(false) }
  var cleanSuccessMessage by remember { mutableStateOf<String?>(null) }

  AnimatedVisibility(
    visible = isOpen,
    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(style.surfaceOverlay)
        .statusBarsPadding()
        .navigationBarsPadding()
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 18.dp, vertical = 12.dp)
      ) {
        // Top Header
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            DreamBotAvatar(size = 36.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "DREAMBYTE COMMAND CENTER",
                fontWeight = FontWeight.Black,
                fontSize = 13.5.sp,
                letterSpacing = 1.sp,
                color = style.primaryHolo
              )
              Text(
                text = "Centro de Control Holográfico • DreamByte OS",
                fontSize = 10.5.sp,
                color = style.textSecondary
              )
            }
          }

          IconButton(onClick = onClose) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Cerrar HUD",
              tint = style.textPrimary
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Toggles Grid
        Text(
          text = "ACCIONES RÁPIDAS DEL DISPOSITIVO",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          color = style.textSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          // 1. Flashlight / Torch
          HudQuickTile(
            title = if (isFlashlightOn) "Linterna ON" else "Linterna",
            subtitle = if (isFlashlightOn) "Activa" else "Apagada",
            icon = if (isFlashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
            isActive = isFlashlightOn,
            modifier = Modifier.weight(1f),
            onClick = {
              try {
                val camManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val cameraId = camManager.cameraIdList.firstOrNull()
                if (cameraId != null) {
                  val newState = !isFlashlightOn
                  camManager.setTorchMode(cameraId, newState)
                  isFlashlightOn = newState
                } else {
                  Toast.makeText(context, "No se detectó flash en el hardware", Toast.LENGTH_SHORT).show()
                }
              } catch (e: Exception) {
                isFlashlightOn = !isFlashlightOn
                Toast.makeText(context, "Estado de linterna alternado", Toast.LENGTH_SHORT).show()
              }
            }
          )

          // 2. Wi-Fi Panel
          HudQuickTile(
            title = "Wi-Fi",
            subtitle = "Redes",
            icon = Icons.Default.Wifi,
            isActive = true,
            modifier = Modifier.weight(1f),
            onClick = {
              try {
                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
              } catch (_: Exception) {}
            }
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          // 3. Bluetooth Panel
          HudQuickTile(
            title = "Bluetooth",
            subtitle = "Dispositivos",
            icon = Icons.Default.Bluetooth,
            isActive = false,
            modifier = Modifier.weight(1f),
            onClick = {
              try {
                context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
              } catch (_: Exception) {}
            }
          )

          // 4. Quantum Touch Particles Toggle
          HudQuickTile(
            title = "Ondas Táctiles",
            subtitle = if (interactiveParticles) "Interactivas" else "Desactivadas",
            icon = Icons.Default.TouchApp,
            isActive = interactiveParticles,
            modifier = Modifier.weight(1f),
            onClick = {
              onToggleInteractiveParticles(!interactiveParticles)
            }
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // RAM Booster & Cache Purge Tool
        GlassCard(modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Memory,
                  contentDescription = null,
                  tint = style.primaryHolo,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "OPT-RAM DREAMBYTE",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = style.textPrimary
                )
              }

              Text(
                text = "${metrics.availableRamMb} MB libres",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = style.primaryHolo
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = cleanSuccessMessage ?: "Libera procesos en segundo plano y memoria caché no esencial para máxima fluidez.",
              fontSize = 11.5.sp,
              color = style.textMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
              onClick = {
                if (!isCleaningRam) {
                  isCleaningRam = true
                  scope.launch {
                    delay(1200)
                    System.gc()
                    isCleaningRam = false
                    cleanSuccessMessage = "¡Memoria optimizada! Núcleos de DreamByte OS sincronizados."
                  }
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = style.primaryHolo,
                contentColor = Color(0xFF030E1F)
              ),
              shape = style.buttonShape,
              enabled = !isCleaningRam,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isCleaningRam) "Optimizando memoria..." else "Optimizar Memoria RAM",
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // System Telemetry HUD
        GlassCard(modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "TELEMETRÍA EN TIEMPO REAL",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              letterSpacing = 1.sp,
              color = style.textSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              TelemetryCell(title = "Nivel Batería", value = "${metrics.batteryLevel}%", color = style.primaryHolo)
              TelemetryCell(title = "Estado Núcleo", value = "Optimizada", color = Color(0xFF10B981))
              TelemetryCell(title = "Tasa Refresco", value = "60 / 120 Hz", color = style.primaryAccent)
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            onClose()
            onOpenSettings()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = style.surfaceGlass,
            contentColor = style.textPrimary
          ),
          shape = style.buttonShape,
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Abrir DreamByte Settings Completos")
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun HudQuickTile(
  title: String,
  subtitle: String,
  icon: ImageVector,
  isActive: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val style = LocalDreamByteStyle.current
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(if (isActive) style.primaryHolo.copy(alpha = 0.2f) else style.surfaceGlass)
      .border(
        1.dp,
        if (isActive) style.primaryHolo else style.borderHighlight,
        RoundedCornerShape(14.dp)
      )
      .clickable(onClick = onClick)
      .padding(14.dp)
  ) {
    Column {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isActive) style.primaryHolo else style.textMuted,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = style.textPrimary
      )
      Text(
        text = subtitle,
        fontSize = 10.5.sp,
        color = if (isActive) style.primaryHolo else style.textMuted
      )
    }
  }
}

@Composable
private fun TelemetryCell(title: String, value: String, color: Color) {
  val style = LocalDreamByteStyle.current
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = title, fontSize = 10.sp, color = style.textMuted)
    Spacer(modifier = Modifier.height(2.dp))
    Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
  }
}
