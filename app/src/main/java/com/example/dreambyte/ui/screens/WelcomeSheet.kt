package com.example.dreambyte.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreambyte.ui.components.DreamBotAvatar
import com.example.dreambyte.ui.theme.LocalDreamByteStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeSheet(
  onGetStarted: () -> Unit
) {
  val style = LocalDreamByteStyle.current

  BasicAlertDialog(
    onDismissRequest = onGetStarted
  ) {
    Box(
      modifier = Modifier
        .widthIn(max = 380.dp)
        .shadow(28.dp, style.cardShape, spotColor = style.glowColor)
        .clip(style.cardShape)
        .background(style.surfaceOverlay)
        .border(style.borderStrokeWidth, style.borderHighlight, style.cardShape)
        .padding(24.dp)
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        DreamBotAvatar(size = 80.dp)

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "¡Te damos la bienvenida!",
          fontWeight = FontWeight.Bold,
          fontSize = 20.sp,
          color = style.textPrimary,
          textAlign = TextAlign.Center
        )

        Text(
          text = "DreamByte Launcher • DreamByte OS",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = style.primaryHolo,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Soy DreamBot, tu guía y mascota oficial de DreamByte Studios. Estás probando la primera versión de la capa de personalización de DreamByte OS.",
          fontSize = 13.sp,
          color = style.textSecondary,
          textAlign = TextAlign.Center,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(style.surfaceGlass)
            .border(1.dp, style.borderHighlight, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Column {
            BulletItem(text = "3 Generaciones visuales: Modern, Holo y Classic.")
            BulletItem(text = "Widgets inteligentes de reloj, batería y diagnóstico.")
            BulletItem(text = "Cajón de aplicaciones y carpetas en el escritorio.")
            BulletItem(text = "Preparado para teléfonos, DreamTab y DreamPad.")
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = onGetStarted,
          colors = ButtonDefaults.buttonColors(
            containerColor = style.primaryHolo,
            contentColor = Color(0xFF030D1B)
          ),
          shape = style.buttonShape,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Explorar mi Escritorio", fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.width(6.dp))
          Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
        }
      }
    }
  }
}

@Composable
private fun BulletItem(text: String) {
  val style = LocalDreamByteStyle.current
  Row(
    verticalAlignment = Alignment.Top,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp)
  ) {
    Icon(
      imageVector = Icons.Default.AutoAwesome,
      contentDescription = null,
      tint = style.primaryHolo,
      modifier = Modifier
        .size(13.dp)
        .padding(top = 2.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = text,
      fontSize = 11.5.sp,
      color = style.textPrimary,
      lineHeight = 15.sp
    )
  }
}
