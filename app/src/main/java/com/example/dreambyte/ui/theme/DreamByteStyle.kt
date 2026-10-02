package com.example.dreambyte.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dreambyte.model.DreamByteAppearance

data class DreamByteTokens(
  val appearance: DreamByteAppearance,
  // Primary brand colors
  val primaryHolo: Color,
  val primaryAccent: Color,
  val secondaryAccent: Color,
  // Backgrounds
  val backgroundDark: Color,
  val surfaceGlass: Color,
  val surfaceCard: Color,
  val surfaceOverlay: Color,
  // Borders & Glow
  val borderHighlight: Color,
  val borderStrokeWidth: Dp,
  val glowColor: Color,
  // Text
  val textPrimary: Color,
  val textSecondary: Color,
  val textMuted: Color,
  // Shapes
  val cardShape: Shape,
  val buttonShape: Shape,
  val dockShape: Shape,
  // Gradients
  val primaryGradient: Brush,
  val cardGlassGradient: Brush,
  val reflectionHighlight: Brush,
  // Holo & Classic specific
  val dividerColor: Color,
  val isRetro: Boolean
)

val ModernTokens = DreamByteTokens(
  appearance = DreamByteAppearance.MODERN,
  primaryHolo = Color(0xFF00F0FF),         // Holographic Electric Cyan
  primaryAccent = Color(0xFF38BDF8),       // Azure Sky Blue
  secondaryAccent = Color(0xFF6366F1),     // Cyber Indigo
  backgroundDark = Color(0xFF060B14),      // Deep Obsidian Navy
  surfaceGlass = Color(0x2E102038),        // Translucent Liquid Glass
  surfaceCard = Color(0x3813294B),         // Elevated Frosted Glass
  surfaceOverlay = Color(0x750A1426),      // Deep Glass Overlay
  borderHighlight = Color(0x5538BDF8),     // Holographic Luminous Edge
  borderStrokeWidth = 1.2.dp,
  glowColor = Color(0x4000F0FF),
  textPrimary = Color(0xFFF0F9FF),
  textSecondary = Color(0xFFBAE6FD),
  textMuted = Color(0xFF7DD3FC).copy(alpha = 0.65f),
  cardShape = RoundedCornerShape(22.dp),
  buttonShape = RoundedCornerShape(16.dp),
  dockShape = RoundedCornerShape(28.dp),
  primaryGradient = Brush.horizontalGradient(
    listOf(Color(0xFF0284C7), Color(0xFF00F0FF))
  ),
  cardGlassGradient = Brush.verticalGradient(
    listOf(Color(0x3838BDF8), Color(0x180E2342))
  ),
  reflectionHighlight = Brush.verticalGradient(
    listOf(Color(0x44FFFFFF), Color(0x00FFFFFF))
  ),
  dividerColor = Color(0x2538BDF8),
  isRetro = false
)

val HoloTokens = DreamByteTokens(
  appearance = DreamByteAppearance.HOLO,
  primaryHolo = Color(0xFF33B5E5),         // Iconic Android Holo Blue
  primaryAccent = Color(0xFF0099CC),       // Deep Holo Cyan
  secondaryAccent = Color(0xFF00DDFF),
  backgroundDark = Color(0xFF000000),      // Pure Android 4.x Black
  surfaceGlass = Color(0xCC081018),        // Dark Charcoal Surface
  surfaceCard = Color(0xDD111C28),         // Angular Holo Panel
  surfaceOverlay = Color(0xEE050B12),
  borderHighlight = Color(0xFF33B5E5),     // Sharp Holo Line
  borderStrokeWidth = 1.5.dp,
  glowColor = Color(0x5533B5E5),
  textPrimary = Color(0xFFFFFFFF),
  textSecondary = Color(0xFF33B5E5),
  textMuted = Color(0xFF888888),
  cardShape = RoundedCornerShape(4.dp),    // Sharp Android 4.0 edges
  buttonShape = RoundedCornerShape(2.dp),
  dockShape = RoundedCornerShape(0.dp),    // Flat Holo bar
  primaryGradient = Brush.horizontalGradient(
    listOf(Color(0xFF0099CC), Color(0xFF33B5E5))
  ),
  cardGlassGradient = Brush.verticalGradient(
    listOf(Color(0xFF142436), Color(0xFF0B141F))
  ),
  reflectionHighlight = Brush.verticalGradient(
    listOf(Color(0x2233B5E5), Color(0x00000000))
  ),
  dividerColor = Color(0xFF33B5E5),
  isRetro = true
)

val ClassicTokens = DreamByteTokens(
  appearance = DreamByteAppearance.CLASSIC,
  primaryHolo = Color(0xFFA4C639),         // Classic Android Green
  primaryAccent = Color(0xFFFF8800),       // Classic Android Orange
  secondaryAccent = Color(0xFF558800),
  backgroundDark = Color(0xFF121415),      // Dark Slate Grey
  surfaceGlass = Color(0xEE2A2E33),        // Beveled Early Android Panel
  surfaceCard = Color(0xFF343940),
  surfaceOverlay = Color(0xF21C1E22),
  borderHighlight = Color(0xFF5A626A),     // Embossed edge
  borderStrokeWidth = 2.dp,
  glowColor = Color(0x33A4C639),
  textPrimary = Color(0xFFEEEEEE),
  textSecondary = Color(0xFFA4C639),
  textMuted = Color(0xFF9E9E9E),
  cardShape = RoundedCornerShape(10.dp),   // Classic rounded button
  buttonShape = RoundedCornerShape(8.dp),
  dockShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
  primaryGradient = Brush.verticalGradient(
    listOf(Color(0xFF4A525A), Color(0xFF23272B))
  ),
  cardGlassGradient = Brush.verticalGradient(
    listOf(Color(0xFF424952), Color(0xFF282C31))
  ),
  reflectionHighlight = Brush.verticalGradient(
    listOf(Color(0x33FFFFFF), Color(0x00000000))
  ),
  dividerColor = Color(0xFF485058),
  isRetro = true
)

val LocalDreamByteStyle = staticCompositionLocalOf { ModernTokens }

@Composable
fun DreamByteThemeProvider(
  appearance: DreamByteAppearance,
  content: @Composable () -> Unit
) {
  val tokens = when (appearance) {
    DreamByteAppearance.MODERN -> ModernTokens
    DreamByteAppearance.HOLO -> HoloTokens
    DreamByteAppearance.CLASSIC -> ClassicTokens
  }

  CompositionLocalProvider(LocalDreamByteStyle provides tokens) {
    content()
  }
}
