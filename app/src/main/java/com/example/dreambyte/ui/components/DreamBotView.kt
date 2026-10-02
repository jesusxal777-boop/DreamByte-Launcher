package com.example.dreambyte.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.dreambyte.model.DreamByteAppearance
import com.example.dreambyte.ui.theme.LocalDreamByteStyle

private val DreamBotQuotes = listOf(
  "¡Hola! Soy DreamBot, la mascota de DreamByte Studios. ¡Bienvenido a tu nuevo escritorio!",
  "Tip: Mantén presionado un icono para moverlo, desinstalarlo o agregarlo a una carpeta.",
  "DreamByte OS Roadmap: Launcher → SystemUI → Framework → Sistema Operativo completo.",
  "¿Sabías que la IA de DreamByte se llama Byte AI? Yo solo soy tu mascota guía.",
  "Cambia la estética en 'DreamByte Settings' entre Modern, Holo y Classic.",
  "DreamByte detecta automáticamente si estás en un smartphone, DreamTab o DreamPad."
)

@Composable
fun DreamBotCompanionCard(
  modifier: Modifier = Modifier,
  onOpenSettings: () -> Unit
) {
  val style = LocalDreamByteStyle.current
  var quoteIndex by remember { mutableIntStateOf(0) }

  val infiniteTransition = rememberInfiniteTransition(label = "dreambot_float")
  val floatAnim by infiniteTransition.animateFloat(
    initialValue = -3f,
    targetValue = 3f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "float"
  )

  GlassCard(
    modifier = modifier.fillMaxWidth(),
    onClick = {
      quoteIndex = (quoteIndex + 1) % DreamBotQuotes.size
    }
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
      // Mascot avatar
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .offset(y = floatAnim.dp)
          .size(54.dp)
      ) {
        // Glow behind mascot
        if (style.appearance == DreamByteAppearance.MODERN) {
          Box(
            modifier = Modifier
              .size(50.dp)
              .shadow(12.dp, CircleShape, spotColor = style.primaryHolo)
          )
        }

        Image(
          painter = painterResource(id = R.drawable.img_dreambot_mascot),
          contentDescription = "DreamBot Mascota",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .border(
              width = 1.5.dp,
              color = style.primaryHolo,
              shape = CircleShape
            )
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Bubble content
      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "DreamBot",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = style.primaryHolo
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(style.primaryHolo.copy(alpha = 0.2f))
                .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
              Text(
                text = "Mascota Oficial",
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = style.textSecondary
              )
            }
          }

          Text(
            text = "Toca para tip",
            fontSize = 10.sp,
            color = style.textMuted
          )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
          text = DreamBotQuotes[quoteIndex],
          fontSize = 11.5.sp,
          color = style.textPrimary,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          lineHeight = 15.sp
        )
      }
    }
  }
}

@Composable
fun DreamBotAvatar(
  size: Dp = 40.dp,
  modifier: Modifier = Modifier
) {
  val style = LocalDreamByteStyle.current
  Image(
    painter = painterResource(id = R.drawable.img_dreambot_mascot),
    contentDescription = "DreamBot",
    contentScale = ContentScale.Crop,
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .border(1.5.dp, style.primaryHolo, CircleShape)
  )
}

@Composable
fun DreamBotEmptyState(
  title: String = "No se encontraron aplicaciones",
  subtitle: String = "DreamBot revisó todo el sistema pero no halló coincidencias."
) {
  val style = LocalDreamByteStyle.current
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier
      .fillMaxWidth()
      .padding(32.dp)
  ) {
    DreamBotAvatar(size = 72.dp)
    Spacer(modifier = Modifier.height(14.dp))
    Text(
      text = title,
      fontWeight = FontWeight.Bold,
      fontSize = 16.sp,
      color = style.textPrimary
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = subtitle,
      fontSize = 13.sp,
      color = style.textMuted,
      lineHeight = 18.sp
    )
  }
}
