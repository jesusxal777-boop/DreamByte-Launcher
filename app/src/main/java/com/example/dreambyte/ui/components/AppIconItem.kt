package com.example.dreambyte.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreambyte.model.DreamByteAppearance
import com.example.dreambyte.model.IconSize
import com.example.dreambyte.ui.theme.LocalDreamByteStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconItem(
  label: String,
  iconBitmap: Bitmap?,
  modifier: Modifier = Modifier,
  iconSize: IconSize = IconSize.NORMAL,
  showLabel: Boolean = true,
  isFolder: Boolean = false,
  folderIcons: List<Bitmap?> = emptyList(),
  onClick: () -> Unit,
  onLongClick: (() -> Unit)? = null
) {
  val style = LocalDreamByteStyle.current
  val baseIconDimension = when (iconSize) {
    IconSize.COMPACT -> 44.dp
    IconSize.NORMAL -> 52.dp
    IconSize.LARGE -> 60.dp
  }

  val interactionSource = remember { MutableInteractionSource() }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .combinedClickable(
        interactionSource = interactionSource,
        indication = ripple(color = style.primaryHolo),
        onClick = onClick,
        onLongClick = onLongClick
      )
      .padding(vertical = 4.dp, horizontal = 2.dp)
      .testTag("app_icon_${label.replace(" ", "_")}")
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.size(baseIconDimension + 8.dp)
    ) {
      if (isFolder) {
        // Folder preview
        FolderPreviewIcon(
          folderIcons = folderIcons,
          dimension = baseIconDimension,
          appearance = style.appearance
        )
      } else {
        // Standard App Icon
        if (iconBitmap != null) {
          // Subtle glow behind icon in Modern mode
          if (style.appearance == DreamByteAppearance.MODERN) {
            Box(
              modifier = Modifier
                .size(baseIconDimension)
                .shadow(
                  elevation = 8.dp,
                  shape = RoundedCornerShape(14.dp),
                  spotColor = style.glowColor,
                  ambientColor = Color.Transparent
                )
            )
          }

          Image(
            bitmap = iconBitmap.asImageBitmap(),
            contentDescription = label,
            modifier = Modifier
              .size(baseIconDimension)
              .clip(
                when (style.appearance) {
                  DreamByteAppearance.MODERN -> RoundedCornerShape(14.dp)
                  DreamByteAppearance.HOLO -> RoundedCornerShape(4.dp)
                  DreamByteAppearance.CLASSIC -> RoundedCornerShape(8.dp)
                }
              )
          )
        } else {
          // Fallback vector icon
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(baseIconDimension)
              .clip(RoundedCornerShape(12.dp))
              .background(style.surfaceCard)
              .border(1.dp, style.borderHighlight, RoundedCornerShape(12.dp))
          ) {
            Icon(
              imageVector = Icons.Default.Android,
              contentDescription = label,
              tint = style.primaryHolo,
              modifier = Modifier.size(baseIconDimension * 0.65f)
            )
          }
        }
      }
    }

    if (showLabel) {
      Spacer(modifier = Modifier.height(3.dp))
      Text(
        text = label,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        style = TextStyle(
          fontSize = when (iconSize) {
            IconSize.COMPACT -> 11.sp
            IconSize.NORMAL -> 12.sp
            IconSize.LARGE -> 13.sp
          },
          fontWeight = when (style.appearance) {
            DreamByteAppearance.MODERN -> FontWeight.Medium
            DreamByteAppearance.HOLO -> FontWeight.Normal
            DreamByteAppearance.CLASSIC -> FontWeight.Bold
          },
          fontFamily = if (style.appearance == DreamByteAppearance.HOLO) FontFamily.SansSerif else FontFamily.Default,
          color = style.textPrimary,
          shadow = when (style.appearance) {
            DreamByteAppearance.MODERN -> Shadow(
              color = Color.Black.copy(alpha = 0.85f),
              offset = Offset(0f, 2f),
              blurRadius = 4f
            )
            DreamByteAppearance.HOLO -> Shadow(
              color = Color.Black,
              offset = Offset(0f, 1f),
              blurRadius = 2f
            )
            DreamByteAppearance.CLASSIC -> Shadow(
              color = Color.Black,
              offset = Offset(1f, 1f),
              blurRadius = 1f
            )
          }
        ),
        modifier = Modifier.padding(horizontal = 2.dp)
      )
    }
  }
}

@Composable
private fun FolderPreviewIcon(
  folderIcons: List<Bitmap?>,
  dimension: Dp,
  appearance: DreamByteAppearance
) {
  val style = LocalDreamByteStyle.current
  val shape = when (appearance) {
    DreamByteAppearance.MODERN -> RoundedCornerShape(16.dp)
    DreamByteAppearance.HOLO -> RoundedCornerShape(4.dp)
    DreamByteAppearance.CLASSIC -> RoundedCornerShape(8.dp)
  }

  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .size(dimension)
      .clip(shape)
      .background(style.surfaceCard)
      .border(
        width = style.borderStrokeWidth,
        color = style.borderHighlight,
        shape = shape
      )
      .padding(4.dp)
  ) {
    if (folderIcons.isEmpty()) {
      Icon(
        imageVector = Icons.Default.Folder,
        contentDescription = "Carpeta",
        tint = style.primaryHolo,
        modifier = Modifier.size(dimension * 0.6f)
      )
    } else {
      // 2x2 mini icons grid inside folder
      Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
      ) {
        Row(
          horizontalArrangement = Arrangement.SpaceEvenly,
          modifier = Modifier.fillMaxSize().weight(1f)
        ) {
          folderIcons.getOrNull(0)?.let { bmp ->
            Image(
              bitmap = bmp.asImageBitmap(),
              contentDescription = null,
              modifier = Modifier.size(dimension * 0.35f).clip(RoundedCornerShape(4.dp))
            )
          }
          folderIcons.getOrNull(1)?.let { bmp ->
            Image(
              bitmap = bmp.asImageBitmap(),
              contentDescription = null,
              modifier = Modifier.size(dimension * 0.35f).clip(RoundedCornerShape(4.dp))
            )
          }
        }
        Row(
          horizontalArrangement = Arrangement.SpaceEvenly,
          modifier = Modifier.fillMaxSize().weight(1f)
        ) {
          folderIcons.getOrNull(2)?.let { bmp ->
            Image(
              bitmap = bmp.asImageBitmap(),
              contentDescription = null,
              modifier = Modifier.size(dimension * 0.35f).clip(RoundedCornerShape(4.dp))
            )
          }
          folderIcons.getOrNull(3)?.let { bmp ->
            Image(
              bitmap = bmp.asImageBitmap(),
              contentDescription = null,
              modifier = Modifier.size(dimension * 0.35f).clip(RoundedCornerShape(4.dp))
            )
          }
        }
      }
    }
  }
}
