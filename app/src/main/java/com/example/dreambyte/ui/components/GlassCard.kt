package com.example.dreambyte.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dreambyte.model.DreamByteAppearance
import com.example.dreambyte.ui.theme.LocalDreamByteStyle

@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  shape: Shape? = null,
  borderStroke: BorderStroke? = null,
  onClick: (() -> Unit)? = null,
  content: @Composable BoxScope.() -> Unit
) {
  val style = LocalDreamByteStyle.current
  val cardShape = shape ?: style.cardShape
  val interactionSource = remember { MutableInteractionSource() }

  val actualBorder = borderStroke ?: when (style.appearance) {
    DreamByteAppearance.MODERN -> BorderStroke(
      style.borderStrokeWidth,
      Brush.linearGradient(
        colors = listOf(
          style.borderHighlight,
          style.borderHighlight.copy(alpha = 0.15f),
          style.primaryHolo.copy(alpha = 0.6f)
        )
      )
    )
    DreamByteAppearance.HOLO -> BorderStroke(
      style.borderStrokeWidth,
      style.borderHighlight
    )
    DreamByteAppearance.CLASSIC -> BorderStroke(
      style.borderStrokeWidth,
      style.borderHighlight
    )
  }

  val baseModifier = modifier
    .then(
      if (style.appearance == DreamByteAppearance.MODERN) {
        Modifier.shadow(
          elevation = 10.dp,
          shape = cardShape,
          spotColor = style.glowColor,
          ambientColor = Color.Black
        )
      } else Modifier
    )
    .clip(cardShape)
    .background(
      brush = style.cardGlassGradient,
      shape = cardShape
    )
    .border(actualBorder, cardShape)
    .then(
      if (onClick != null) {
        Modifier.clickable(
          interactionSource = interactionSource,
          indication = ripple(color = style.primaryHolo),
          onClick = onClick
        )
      } else Modifier
    )

  Box(modifier = baseModifier) {
    // Specular top highlight for Liquid Glass effect in Modern mode
    if (style.appearance == DreamByteAppearance.MODERN) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(28.dp)
          .background(style.reflectionHighlight)
      )
    } else if (style.appearance == DreamByteAppearance.HOLO) {
      // Top thin accent bar for Holo cards
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(2.dp)
          .background(style.primaryHolo)
      )
    }
    content()
  }
}
