package com.example.dreambyte.ui.components

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreambyte.model.WidgetType
import com.example.dreambyte.ui.theme.LocalDreamByteStyle

data class WidgetCatalogItem(
  val type: WidgetType,
  val title: String,
  val description: String,
  val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetPickerDialog(
  currentPage: Int,
  onDismiss: () -> Unit,
  onSelectWidget: (WidgetType) -> Unit
) {
  val style = LocalDreamByteStyle.current

  val catalog = listOf(
    WidgetCatalogItem(
      type = WidgetType.CLOCK,
      title = "Reloj Holográfico",
      description = "Muestra la hora en tiempo real, segundos y fecha completa estilo DreamByte OS.",
      icon = Icons.Default.Schedule
    ),
    WidgetCatalogItem(
      type = WidgetType.WEATHER,
      title = "Clima y Pronóstico Real",
      description = "Temperatura actual, condiciones, sensación térmica y pronóstico extendido.",
      icon = Icons.Default.WbSunny
    ),
    WidgetCatalogItem(
      type = WidgetType.BATTERY,
      title = "Batería DreamByte",
      description = "Nivel de batería, estado de carga rápida e indicador dinámico de salud.",
      icon = Icons.Default.BatteryFull
    ),
    WidgetCatalogItem(
      type = WidgetType.DEVICE_INFO,
      title = "Diagnóstico del Sistema",
      description = "Métricas en tiempo real de RAM consumida, almacenamiento y versión de OS.",
      icon = Icons.Default.Memory
    ),
    WidgetCatalogItem(
      type = WidgetType.MEDIA_PLAYER,
      title = "Reproductor Multimedia",
      description = "Controles de música, barras de ecualizador animadas y visualizador.",
      icon = Icons.Default.MusicNote
    ),
    WidgetCatalogItem(
      type = WidgetType.QUICK_NOTES,
      title = "Notas Rápidas",
      description = "Bloc de notas interactivo y autoguardado directamente en el escritorio.",
      icon = Icons.Default.EditNote
    ),
    WidgetCatalogItem(
      type = WidgetType.DREAMBOT,
      title = "DreamBot Mascota Guía",
      description = "La simpática mascota interactiva de DreamByte Studios con consejos holográficos.",
      icon = Icons.Default.SmartToy
    ),
    WidgetCatalogItem(
      type = WidgetType.QUICK_SEARCH,
      title = "Búsqueda Rápida",
      description = "Barra de acceso inmediato para buscar apps y abrir el Centro de Control.",
      icon = Icons.Default.Search
    )
  )

  BasicAlertDialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .widthIn(min = 300.dp, max = 420.dp)
        .shadow(24.dp, style.cardShape, spotColor = style.glowColor)
        .clip(style.cardShape)
        .background(style.surfaceOverlay)
        .border(style.borderStrokeWidth, style.borderHighlight, style.cardShape)
        .padding(20.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column {
            Text(
              text = "Agregar Widget",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = style.textPrimary
            )
            Text(
              text = "Se colocará en Página ${currentPage + 1}",
              fontSize = 11.5.sp,
              color = style.primaryHolo
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Cerrar",
              tint = style.textSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(catalog, key = { it.type.name }) { item ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(style.surfaceGlass)
                .border(0.8.dp, style.borderHighlight, RoundedCornerShape(12.dp))
                .clickable {
                  onSelectWidget(item.type)
                  onDismiss()
                }
                .padding(12.dp)
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(style.primaryHolo.copy(alpha = 0.2f))
                  .border(0.8.dp, style.primaryHolo.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
              ) {
                Icon(
                  imageVector = item.icon,
                  contentDescription = null,
                  tint = style.primaryHolo,
                  modifier = Modifier.size(22.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = item.title,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = style.textPrimary
                )
                Text(
                  text = item.description,
                  fontSize = 11.sp,
                  color = style.textSecondary,
                  lineHeight = 14.sp
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(30.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(style.primaryHolo)
              ) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "Agregar",
                  tint = Color(0xFF020D1D),
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}
