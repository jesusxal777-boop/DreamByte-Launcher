package com.example.dreambyte.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreambyte.data.WallpaperHelper
import com.example.dreambyte.model.DeviceType
import com.example.dreambyte.model.DreamByteAppearance
import com.example.dreambyte.model.IconSize
import com.example.dreambyte.model.LauncherSettings
import com.example.dreambyte.model.WallpaperType
import com.example.dreambyte.ui.components.DreamBotAvatar
import com.example.dreambyte.ui.components.GlassCard
import com.example.dreambyte.ui.theme.LocalDreamByteStyle

@Composable
fun SettingsScreen(
  settings: LauncherSettings,
  isDefaultLauncher: Boolean,
  onBack: () -> Unit,
  onSetAppearance: (DreamByteAppearance) -> Unit,
  onSetWallpaper: (WallpaperType) -> Unit,
  onSetCustomWallpaper: (String) -> Unit,
  onToggleInteractiveParticles: (Boolean) -> Unit,
  onSetGrid: (rows: Int, cols: Int) -> Unit,
  onSetIconSize: (IconSize) -> Unit,
  onToggleAnimations: (Boolean) -> Unit,
  onToggleDreamBot: (Boolean) -> Unit,
  onSetDeviceMode: (DeviceType) -> Unit,
  onOpenHud: () -> Unit,
  onRequestSetDefault: () -> Unit,
  onResetDefaults: () -> Unit
) {
  BackHandler { onBack() }

  val style = LocalDreamByteStyle.current
  val scrollState = rememberScrollState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(style.backgroundDark)
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
      // Top Bar
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        IconButton(onClick = onBack) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Regresar",
            tint = style.textPrimary
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "DreamByte Settings",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = style.textPrimary
          )
          Text(
            text = "DreamByte Studios • Personalización de DreamByte OS",
            fontSize = 11.sp,
            color = style.primaryHolo
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Section: Default Launcher Role
      SettingsSectionHeader(title = "APLICACIÓN DE INICIO PREDETERMINADA", icon = Icons.Default.Home)
      Spacer(modifier = Modifier.height(8.dp))
      GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (isDefaultLauncher) Icons.Default.CheckCircle else Icons.Default.Info,
              contentDescription = null,
              tint = if (isDefaultLauncher) Color(0xFF10B981) else style.primaryHolo,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = if (isDefaultLauncher) "DreamByte es tu launcher predeterminado" else "DreamByte no está establecido como predeterminado",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = style.textPrimary
              )
              Text(
                text = if (isDefaultLauncher) "Todos los accesos al botón Home abren DreamByte OS" else "Presiona el botón abajo para activarlo al pulsar Home",
                fontSize = 11.5.sp,
                color = style.textMuted
              )
            }
          }

          if (!isDefaultLauncher) {
            Spacer(modifier = Modifier.height(14.dp))
            Button(
              onClick = onRequestSetDefault,
              colors = ButtonDefaults.buttonColors(
                containerColor = style.primaryHolo,
                contentColor = Color(0xFF021024)
              ),
              shape = style.buttonShape,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Establecer como Launcher Predeterminado", fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Section: DreamByte Appearance Generations
      SettingsSectionHeader(title = "DREAMBYTE APPEARANCE", icon = Icons.Default.FormatPaint)
      Text(
        text = "Selecciona la generación visual del sistema:",
        fontSize = 12.sp,
        color = style.textMuted,
        modifier = Modifier.padding(bottom = 10.dp)
      )

      DreamByteAppearance.entries.forEach { appearance ->
        val isSelected = settings.appearance == appearance
        AppearanceCard(
          appearance = appearance,
          isSelected = isSelected,
          onClick = { onSetAppearance(appearance) }
        )
        Spacer(modifier = Modifier.height(10.dp))
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Section: Wallpapers
      SettingsSectionHeader(title = "FONDO DE PANTALLA", icon = Icons.Default.Wallpaper)
      Spacer(modifier = Modifier.height(8.dp))

      val context = LocalContext.current
      val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
      ) { uri ->
        if (uri != null) {
          val savedPath = WallpaperHelper.saveCustomWallpaperFromUri(context, uri)
          if (savedPath != null) {
            onSetCustomWallpaper(savedPath)
          }
        }
      }

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        WallpaperType.entries.forEach { wp ->
          val isSelected = settings.wallpaperType == wp
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) style.surfaceCard else style.surfaceGlass)
              .border(
                1.dp,
                if (isSelected) style.primaryHolo else style.borderHighlight,
                RoundedCornerShape(12.dp)
              )
              .clickable { onSetWallpaper(wp) }
              .padding(horizontal = 14.dp, vertical = 12.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Box(
                modifier = Modifier
                  .size(18.dp)
                  .clip(CircleShape)
                  .border(2.dp, if (isSelected) style.primaryHolo else style.textMuted, CircleShape)
                  .padding(3.dp)
              ) {
                if (isSelected) {
                  Box(
                    modifier = Modifier
                      .fillMaxSize()
                      .clip(CircleShape)
                      .background(style.primaryHolo)
                  )
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = wp.displayName,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 14.sp,
                  color = if (isSelected) style.primaryHolo else style.textPrimary
                )

                if (wp == WallpaperType.SYSTEM_DEFAULT) {
                  Text(
                    text = "Aplica el fondo de pantalla actual de tu dispositivo Android",
                    fontSize = 11.sp,
                    color = style.textMuted
                  )
                } else if (wp == WallpaperType.CUSTOM) {
                  Text(
                    text = if (settings.customWallpaperPath != null) "Foto personalizada activa" else "Elige cualquier imagen de tu galería",
                    fontSize = 11.sp,
                    color = style.textMuted
                  )
                }
              }
            }

            // Quick action buttons for system or custom wallpaper
            if (wp == WallpaperType.CUSTOM) {
              Spacer(modifier = Modifier.height(8.dp))
              Button(
                onClick = {
                  photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = style.primaryHolo.copy(alpha = 0.2f),
                  contentColor = style.primaryHolo
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(start = 30.dp)
              ) {
                Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Elegir Imagen de la Galería", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            } else if (wp == WallpaperType.SYSTEM_DEFAULT && isSelected) {
              Spacer(modifier = Modifier.height(8.dp))
              Button(
                onClick = {
                  try {
                    context.startActivity(Intent(Intent.ACTION_SET_WALLPAPER).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                  } catch (_: Exception) {}
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = style.primaryHolo.copy(alpha = 0.2f),
                  contentColor = style.primaryHolo
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(start = 30.dp)
              ) {
                Text("Abrir Selector de Fondos de Android", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Section: Innovative Holographic Quantum Waves
      SettingsSectionHeader(title = "FUNCIONES INNOVADORAS DREAMBYTE", icon = Icons.Default.AutoAwesome)
      Spacer(modifier = Modifier.height(8.dp))
      GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Ondas Táctiles Cuánticas",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = style.textPrimary
              )
              Text(
                text = "Genera ondas luminosas de plasma y partículas holográficas al tocar o deslizar en el fondo de pantalla",
                fontSize = 11.5.sp,
                color = style.textMuted
              )
            }
            Switch(
              checked = settings.interactiveParticlesEnabled,
              onCheckedChange = onToggleInteractiveParticles,
              colors = SwitchDefaults.colors(
                checkedThumbColor = style.primaryHolo,
                checkedTrackColor = style.primaryHolo.copy(alpha = 0.3f)
              )
            )
          }

          Spacer(modifier = Modifier.height(14.dp))
          HorizontalDivider(color = style.borderHighlight.copy(alpha = 0.3f))
          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = onOpenHud,
            colors = ButtonDefaults.buttonColors(
              containerColor = style.primaryHolo,
              contentColor = Color(0xFF030E1F)
            ),
            shape = style.buttonShape,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Abrir Command Center & HUD Holográfico", fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Section: Desktop & Grid
      SettingsSectionHeader(title = "CUADRÍCULA Y ESCRITORIO", icon = Icons.Default.GridOn)
      Spacer(modifier = Modifier.height(8.dp))
      GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Grid columns
          Text(text = "Columnas del escritorio", fontSize = 13.sp, color = style.textSecondary)
          Spacer(modifier = Modifier.height(6.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(4, 5, 6).forEach { cols ->
              GridOptionChip(
                label = "$cols Columnas",
                isSelected = settings.gridCols == cols,
                onClick = { onSetGrid(settings.gridRows, cols) }
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Grid rows
          Text(text = "Filas del escritorio", fontSize = 13.sp, color = style.textSecondary)
          Spacer(modifier = Modifier.height(6.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(4, 5, 6, 7).forEach { rows ->
              GridOptionChip(
                label = "$rows Filas",
                isSelected = settings.gridRows == rows,
                onClick = { onSetGrid(rows, settings.gridCols) }
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Icon size
          Text(text = "Tamaño de iconos", fontSize = 13.sp, color = style.textSecondary)
          Spacer(modifier = Modifier.height(6.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconSize.entries.forEach { size ->
              GridOptionChip(
                label = size.label,
                isSelected = settings.iconSize == size,
                onClick = { onSetIconSize(size) }
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Section: DreamPad & Device Mode
      SettingsSectionHeader(title = "ECOSISTEMA DREAMBYTE & DISPOSITIVOS", icon = Icons.Default.Devices)
      Text(
        text = "DreamByte OS está preparado para teléfonos, tablets (DreamTab) y laptops (DreamPad):",
        fontSize = 12.sp,
        color = style.textMuted,
        modifier = Modifier.padding(bottom = 8.dp)
      )
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DeviceType.entries.forEach { dt ->
          GridOptionChip(
            label = dt.displayName,
            isSelected = settings.deviceModeOverride == dt,
            onClick = { onSetDeviceMode(dt) }
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Section: Animations & Mascot
      SettingsSectionHeader(title = "RENDIMIENTO Y DREAMBOT", icon = Icons.Default.Animation)
      Spacer(modifier = Modifier.height(8.dp))
      GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Animaciones Fluidas",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = style.textPrimary
              )
              Text(
                text = "Desactívalas si usas un dispositivo de gama baja para máxima velocidad",
                fontSize = 11.5.sp,
                color = style.textMuted
              )
            }
            Switch(
              checked = settings.animationsEnabled,
              onCheckedChange = onToggleAnimations,
              colors = SwitchDefaults.colors(
                checkedThumbColor = style.primaryHolo,
                checkedTrackColor = style.primaryHolo.copy(alpha = 0.3f)
              )
            )
          }

          Spacer(modifier = Modifier.height(14.dp))
          HorizontalDivider(color = style.borderHighlight.copy(alpha = 0.3f))
          Spacer(modifier = Modifier.height(14.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Mascota DreamBot",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = style.textPrimary
              )
              Text(
                text = "Mostrar a DreamBot, la mascota oficial, en el escritorio y estados del sistema",
                fontSize = 11.5.sp,
                color = style.textMuted
              )
            }
            Switch(
              checked = settings.showDreamBot,
              onCheckedChange = onToggleDreamBot,
              colors = SwitchDefaults.colors(
                checkedThumbColor = style.primaryHolo,
                checkedTrackColor = style.primaryHolo.copy(alpha = 0.3f)
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Section: About DreamByte Studios & OS Roadmap
      SettingsSectionHeader(title = "ACERCA DE DREAMBYTE STUDIOS", icon = Icons.Default.Info)
      Spacer(modifier = Modifier.height(8.dp))
      GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            DreamBotAvatar(size = 46.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "DreamByte Studios",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = style.textPrimary
              )
              Text(
                text = "DreamByte OS • Fase 1: Launcher",
                fontSize = 12.sp,
                color = style.primaryHolo
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "DreamByte Launcher es el primer paso hacia DreamByte OS. Diseñado con una identidad visual propia, soporte para tablets (DreamTab) y laptops (DreamPad).",
            fontSize = 12.sp,
            color = style.textSecondary,
            lineHeight = 17.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Roadmap Box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(style.surfaceGlass)
              .border(1.dp, style.borderHighlight, RoundedCornerShape(10.dp))
              .padding(12.dp)
          ) {
            Column {
              Text(
                text = "ROADMAP DEL SISTEMA OPERATIVO:",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = style.primaryHolo,
                letterSpacing = 1.sp
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(text = "✔ Paso 1: DreamByte Launcher (Capa de personalización)", fontSize = 11.5.sp, color = style.textPrimary)
              Text(text = "⏳ Paso 2: DreamByte SystemUI (Barra de estado y panel rápido)", fontSize = 11.5.sp, color = style.textMuted)
              Text(text = "⏳ Paso 3: DreamByte Framework (Servicios centrales)", fontSize = 11.5.sp, color = style.textMuted)
              Text(text = "⏳ Paso 4: DreamByte OS (Sistema operativo completo)", fontSize = 11.5.sp, color = style.textMuted)
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "* Nota de identidad: DreamBot es la mascota oficial. La IA de DreamByte se llamará Byte AI.",
                fontSize = 10.5.sp,
                color = style.primaryAccent
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Reset Defaults Button
      OutlinedButton(
        onClick = onResetDefaults,
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = Color(0xFFEF4444)
        ),
        shape = style.buttonShape,
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Restablecer Escritorio y Ajustes de Fábrica", fontWeight = FontWeight.SemiBold)
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}

@Composable
private fun AppearanceCard(
  appearance: DreamByteAppearance,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val style = LocalDreamByteStyle.current
  GlassCard(
    modifier = Modifier.fillMaxWidth(),
    onClick = onClick
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Box(
        modifier = Modifier
          .size(20.dp)
          .clip(CircleShape)
          .border(2.dp, if (isSelected) style.primaryHolo else style.textMuted, CircleShape)
          .padding(3.dp)
      ) {
        if (isSelected) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .clip(CircleShape)
              .background(style.primaryHolo)
          )
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = appearance.title,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = if (isSelected) style.primaryHolo else style.textPrimary
          )
          Spacer(modifier = Modifier.width(8.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(style.primaryHolo.copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = appearance.era,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Medium,
              color = style.textSecondary
            )
          }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = appearance.subtitle,
          fontSize = 11.5.sp,
          color = style.textMuted
        )
      }
    }
  }
}

@Composable
private fun SettingsSectionHeader(title: String, icon: ImageVector) {
  val style = LocalDreamByteStyle.current
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = style.primaryHolo,
      modifier = Modifier.size(16.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = title,
      fontWeight = FontWeight.Bold,
      fontSize = 11.5.sp,
      letterSpacing = 1.sp,
      color = style.textSecondary
    )
  }
}

@Composable
private fun GridOptionChip(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val style = LocalDreamByteStyle.current
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) style.primaryHolo else style.surfaceGlass)
      .border(
        1.dp,
        if (isSelected) style.primaryHolo else style.borderHighlight,
        RoundedCornerShape(8.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    Text(
      text = label,
      fontSize = 12.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      color = if (isSelected) Color(0xFF030D1B) else style.textPrimary
    )
  }
}
