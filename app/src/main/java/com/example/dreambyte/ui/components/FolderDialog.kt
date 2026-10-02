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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreambyte.model.AppInfo
import com.example.dreambyte.model.DreamByteAppearance
import com.example.dreambyte.model.FolderInfo
import com.example.dreambyte.ui.theme.LocalDreamByteStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDialog(
  folder: FolderInfo,
  appsInFolder: List<AppInfo>,
  onDismiss: () -> Unit,
  onLaunchApp: (AppInfo) -> Unit,
  onRenameFolder: (String) -> Unit,
  onDeleteFolder: () -> Unit,
  onExtractApp: ((String) -> Unit)? = null
) {
  val style = LocalDreamByteStyle.current
  var isEditingName by remember { mutableStateOf(false) }
  var editedName by remember(folder.name) { mutableStateOf(folder.name) }
  var appToExtract by remember { mutableStateOf<AppInfo?>(null) }

  BasicAlertDialog(
    onDismissRequest = onDismiss
  ) {
    Box(
      modifier = Modifier
        .widthIn(min = 280.dp, max = 380.dp)
        .shadow(24.dp, style.cardShape, spotColor = style.glowColor)
        .clip(style.cardShape)
        .background(style.surfaceOverlay)
        .border(style.borderStrokeWidth, style.borderHighlight, style.cardShape)
        .padding(20.dp)
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        // Folder Header
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          if (isEditingName) {
            OutlinedTextField(
              value = editedName,
              onValueChange = { editedName = it },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = style.textPrimary,
                unfocusedTextColor = style.textPrimary,
                focusedBorderColor = style.primaryHolo,
                unfocusedBorderColor = style.borderHighlight
              ),
              modifier = Modifier.weight(1f)
            )
            IconButton(
              onClick = {
                isEditingName = false
                if (editedName.isNotBlank()) {
                  onRenameFolder(editedName)
                }
              }
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Guardar",
                tint = style.primaryHolo
              )
            }
          } else {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = folder.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = style.textPrimary
              )
              IconButton(onClick = { isEditingName = true }) {
                Icon(
                  imageVector = Icons.Default.Edit,
                  contentDescription = "Editar nombre",
                  tint = style.textMuted,
                  modifier = Modifier.padding(2.dp)
                )
              }
            }
          }

          Row {
            IconButton(onClick = onDeleteFolder) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar carpeta",
                tint = Color(0xFFEF4444).copy(alpha = 0.8f)
              )
            }
            IconButton(onClick = onDismiss) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar",
                tint = style.textPrimary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (appsInFolder.isEmpty()) {
          Text(
            text = "Carpeta vacía",
            color = style.textMuted,
            fontSize = 13.sp,
            modifier = Modifier.padding(vertical = 24.dp)
          )
        } else {
          LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(240.dp)
          ) {
            items(appsInFolder, key = { it.packageName }) { app ->
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AppIconItem(
                  label = app.label,
                  iconBitmap = app.iconBitmap,
                  onClick = {
                    onDismiss()
                    onLaunchApp(app)
                  },
                  onLongClick = {
                    appToExtract = app
                  }
                )
                if (onExtractApp != null) {
                  Text(
                    text = "Mover fuera",
                    fontSize = 9.sp,
                    color = style.primaryHolo,
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .clickable {
                        onExtractApp(app.packageName)
                      }
                      .padding(horizontal = 4.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
