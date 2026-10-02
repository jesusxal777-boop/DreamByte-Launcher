package com.example.dreambyte.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreambyte.model.AppInfo
import com.example.dreambyte.model.DrawerLayoutMode
import com.example.dreambyte.model.DreamByteAppearance
import com.example.dreambyte.model.IconSize
import com.example.dreambyte.ui.components.AppIconItem
import com.example.dreambyte.ui.components.DreamBotEmptyState
import com.example.dreambyte.ui.theme.LocalDreamByteStyle

@Composable
fun AppDrawerSheet(
  isOpen: Boolean,
  apps: List<AppInfo>,
  searchQuery: String,
  layoutMode: DrawerLayoutMode,
  iconSize: IconSize,
  gridCols: Int,
  onQueryChange: (String) -> Unit,
  onClose: () -> Unit,
  onLaunchApp: (AppInfo) -> Unit,
  onLongClickApp: (AppInfo) -> Unit,
  onToggleLayoutMode: () -> Unit
) {
  BackHandler(enabled = isOpen) {
    onClose()
  }

  val style = LocalDreamByteStyle.current
  var selectedCategory by remember { mutableStateOf("Todas") }

  AnimatedVisibility(
    visible = isOpen,
    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
  ) {
    val displayedApps = remember(apps, selectedCategory) {
      when (selectedCategory) {
        "Sistema" -> apps.filter { it.isSystemApp }
        "Usuario" -> apps.filter { !it.isSystemApp }
        else -> apps
      }
    }

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
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        // Drag handle pill at top
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClose)
            .padding(vertical = 4.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(style.surfaceGlass)
              .padding(horizontal = 14.dp, vertical = 3.dp)
          ) {
            Icon(
              imageVector = Icons.Default.KeyboardArrowDown,
              contentDescription = "Cerrar cajón",
              tint = style.primaryHolo,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Desliza o toca para cerrar",
              fontSize = 11.sp,
              color = style.textMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Header: Search bar + Back + Layout toggle
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          IconButton(onClick = onClose) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Regresar al escritorio",
              tint = style.textPrimary
            )
          }

          // Search text field
          Box(
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(style.surfaceGlass)
              .border(style.borderStrokeWidth, style.borderHighlight, RoundedCornerShape(16.dp))
              .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = style.primaryHolo,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              TextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                placeholder = {
                  Text(
                    text = "Buscar aplicaciones (${displayedApps.size})...",
                    fontSize = 13.5.sp,
                    color = style.textMuted
                  )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                  focusedContainerColor = Color.Transparent,
                  unfocusedContainerColor = Color.Transparent,
                  focusedIndicatorColor = Color.Transparent,
                  unfocusedIndicatorColor = Color.Transparent,
                  focusedTextColor = style.textPrimary,
                  unfocusedTextColor = style.textPrimary
                ),
                modifier = Modifier.weight(1f)
              )

              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Limpiar",
                    tint = style.textSecondary,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Grid / List toggle
          IconButton(onClick = onToggleLayoutMode) {
            Icon(
              imageVector = if (layoutMode == DrawerLayoutMode.GRID) Icons.Default.ViewList else Icons.Default.GridView,
              contentDescription = "Cambiar vista",
              tint = style.primaryHolo
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Categories / Filter Chips
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          listOf("Todas", "Usuario", "Sistema").forEach { cat ->
            val isSelected = selectedCategory == cat
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) style.primaryHolo else style.surfaceGlass)
                .border(
                  0.8.dp,
                  if (isSelected) style.primaryHolo else style.borderHighlight,
                  RoundedCornerShape(10.dp)
                )
                .clickable { selectedCategory = cat }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = cat,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color(0xFF030D1B) else style.textPrimary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Drawer Content
        if (displayedApps.isEmpty()) {
          DreamBotEmptyState(
            title = "Sin resultados",
            subtitle = "DreamBot no encontró aplicaciones que coincidan con tu búsqueda."
          )
        } else {
          when (layoutMode) {
            DrawerLayoutMode.GRID -> {
              LazyVerticalGrid(
                columns = GridCells.Fixed(gridCols.coerceIn(3, 6)),
                contentPadding = PaddingValues(top = 4.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
              ) {
                items(displayedApps, key = { it.packageName }) { app ->
                  AppIconItem(
                    label = app.label,
                    iconBitmap = app.iconBitmap,
                    iconSize = iconSize,
                    onClick = { onLaunchApp(app) },
                    onLongClick = { onLongClickApp(app) }
                  )
                }
              }
            }

            DrawerLayoutMode.LIST -> {
              LazyColumn(
                contentPadding = PaddingValues(top = 4.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxSize()
              ) {
                items(displayedApps, key = { it.packageName }) { app ->
                  DrawerListRow(
                    app = app,
                    onClick = { onLaunchApp(app) },
                    onLongClick = { onLongClickApp(app) }
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

@Composable
private fun DrawerListRow(
  app: AppInfo,
  onClick: () -> Unit,
  onLongClick: () -> Unit
) {
  val style = LocalDreamByteStyle.current
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 6.dp)
  ) {
    AppIconItem(
      label = "",
      iconBitmap = app.iconBitmap,
      showLabel = false,
      onClick = onClick,
      onLongClick = onLongClick
    )

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = app.label,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        color = style.textPrimary
      )
      Text(
        text = app.packageName,
        fontSize = 11.5.sp,
        color = style.textMuted,
        maxLines = 1
      )
    }

    IconButton(onClick = onLongClick) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Opciones",
        tint = style.primaryHolo,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

