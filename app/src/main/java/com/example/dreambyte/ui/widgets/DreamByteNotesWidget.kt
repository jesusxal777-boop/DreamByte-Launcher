package com.example.dreambyte.ui.widgets

import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreambyte.ui.components.GlassCard
import com.example.dreambyte.ui.theme.LocalDreamByteStyle

@Composable
fun DreamByteNotesWidget(
  modifier: Modifier = Modifier
) {
  val style = LocalDreamByteStyle.current
  val context = LocalContext.current
  val prefs = remember { context.getSharedPreferences("dreambyte_widget_notes", Context.MODE_PRIVATE) }

  var noteText by remember {
    mutableStateOf(
      prefs.getString("note_content", "✨ Bienvenido a DreamByte OS.\nPuedes mover widgets y apps libremente en el escritorio.") ?: ""
    )
  }

  GlassCard(modifier = modifier.fillMaxWidth()) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.EditNote,
            contentDescription = null,
            tint = style.primaryHolo,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "NOTAS RÁPIDAS",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = style.textSecondary
          )
        }

        Text(
          text = "Autoguardado",
          fontSize = 10.sp,
          color = style.primaryHolo.copy(alpha = 0.8f)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(style.surfaceGlass.copy(alpha = 0.5f))
          .border(0.6.dp, style.borderHighlight.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
          .padding(10.dp)
      ) {
        BasicTextField(
          value = noteText,
          onValueChange = {
            noteText = it
            prefs.edit().putString("note_content", it).apply()
          },
          textStyle = TextStyle(
            color = style.textPrimary,
            fontSize = 12.5.sp,
            lineHeight = 17.sp
          ),
          cursorBrush = SolidColor(style.primaryHolo),
          modifier = Modifier.fillMaxWidth(),
          minLines = 2,
          maxLines = 4
        )
      }
    }
  }
}
