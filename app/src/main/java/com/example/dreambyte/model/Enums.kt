package com.example.dreambyte.model

enum class DreamByteAppearance(val title: String, val subtitle: String, val era: String) {
  MODERN(
    title = "DreamByte Modern",
    subtitle = "Liquid Glass, Holographic Blue & Cyber Refractions",
    era = "Generación OS Actual"
  ),
  HOLO(
    title = "DreamByte Holo",
    subtitle = "Android 4.x Retro, Electric Cyan & Angular Wireframes",
    era = "Generación 2011-2014"
  ),
  CLASSIC(
    title = "DreamByte Classic",
    subtitle = "Early Android Nostalgia, Metallic Bevels & Amber Accents",
    era = "Generación 2008-2010"
  )
}

enum class DeviceType(val displayName: String, val category: String) {
  AUTO("Auto-detectar", "Detectado por pantalla"),
  PHONE("DreamPhone", "Móvil vertical"),
  DREAM_TAB("DreamTab", "Tablet multitarea"),
  DREAM_PAD("DreamPad", "Laptop / Escritorio OS")
}

enum class WidgetType(val displayName: String) {
  CLOCK("Reloj Holográfico"),
  BATTERY("Estado de Batería"),
  DEVICE_INFO("Diagnóstico del Sistema"),
  DREAMBOT("DreamBot Compañero")
}

enum class DrawerLayoutMode {
  GRID,
  LIST
}

enum class IconSize(val scale: Float, val label: String) {
  COMPACT(0.85f, "Compacto"),
  NORMAL(1.0f, "Normal"),
  LARGE(1.2f, "Grande")
}

enum class WallpaperType(val displayName: String) {
  MODERN_HOLO("DreamByte Modern Wave"),
  CYBER_GRID("DreamByte Holo Cyber Grid"),
  DEEP_VOID("Midnight Deep Void"),
  CLASSIC_AURORA("Classic Retro Space"),
  SYSTEM_DEFAULT("Fondo del Sistema"),
  CUSTOM("Fondo Personalizado (Galería)")
}
