package com.example.dreambyte.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreambyte.model.AppInfo
import com.example.dreambyte.ui.theme.LocalDreamByteStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContextMenu(
  app: AppInfo,
  isOnDesktop: Boolean = false,
  isDocked: Boolean = false,
  onDismiss: () -> Unit,
  onAddToDesktop: () -> Unit,
  onRemoveFromDesktop: () -> Unit,
  onToggleDock: () -> Unit,
  onCreateFolder: () -> Unit,
  onMoveItem: (() -> Unit)? = null,
  onAppInfo: () -> Unit,
  onUninstall: () -> Unit
) {
  val style = LocalDreamByteStyle.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = style.surfaceOverlay,
    contentColor = style.textPrimary,
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(top = 10.dp, bottom = 4.dp)
          .size(width = 36.dp, height = 4.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(style.borderHighlight)
      )
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
      // Header info
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        if (app.iconBitmap != null) {
          Image(
            bitmap = app.iconBitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(12.dp))
          )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = app.label,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = style.textPrimary
          )
          Text(
            text = app.packageName,
            fontSize = 11.5.sp,
            color = style.textMuted,
            maxLines = 1
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
      HorizontalDivider(color = style.borderHighlight.copy(alpha = 0.4f))
      Spacer(modifier = Modifier.height(8.dp))

      // Action items
      if (isOnDesktop) {
        if (onMoveItem != null) {
          MenuActionItem(
            icon = Icons.Default.OpenWith,
            title = "Mover y Reorganizar Posición",
            iconTint = style.primaryHolo,
            onClick = {
              onDismiss()
              onMoveItem()
            }
          )
        }

        MenuActionItem(
          icon = Icons.Default.Delete,
          title = "Eliminar del Escritorio",
          iconTint = Color(0xFFF87171),
          onClick = {
            onDismiss()
            onRemoveFromDesktop()
          }
        )
      } else {
        MenuActionItem(
          icon = Icons.Default.Add,
          title = "Agregar al Escritorio",
          iconTint = style.primaryHolo,
          onClick = {
            onDismiss()
            onAddToDesktop()
          }
        )
      }

      MenuActionItem(
        icon = Icons.Default.PushPin,
        title = if (isDocked) "Desanclar del Dock" else "Anclar al Dock",
        iconTint = style.primaryAccent,
        onClick = {
          onDismiss()
          onToggleDock()
        }
      )

      MenuActionItem(
        icon = Icons.Default.CreateNewFolder,
        title = "Crear Carpeta con esta App",
        iconTint = style.textSecondary,
        onClick = {
          onDismiss()
          onCreateFolder()
        }
      )

      MenuActionItem(
        icon = Icons.Default.Info,
        title = "Información de la Aplicación",
        iconTint = style.textSecondary,
        onClick = {
          onDismiss()
          onAppInfo()
        }
      )

      if (!app.isSystemApp) {
        MenuActionItem(
          icon = Icons.Default.DeleteForever,
          title = "Desinstalar Aplicación",
          iconTint = Color(0xFFEF4444),
          onClick = {
            onDismiss()
            onUninstall()
          }
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun MenuActionItem(
  icon: ImageVector,
  title: String,
  iconTint: Color,
  onClick: () -> Unit
) {
  val style = LocalDreamByteStyle.current
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .clickable(onClick = onClick)
      .padding(vertical = 12.dp, horizontal = 12.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = iconTint,
      modifier = Modifier.size(22.dp)
    )
    Spacer(modifier = Modifier.width(14.dp))
    Text(
      text = title,
      fontSize = 14.5.sp,
      fontWeight = FontWeight.Medium,
      color = style.textPrimary
    )
  }
}
