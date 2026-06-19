package com.zaneschepke.wireguardautotunnel.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFF0D0D0D),
    surface = Color(0xFF1A1A1A),
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun WGTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = androidx.compose.ui.text.font.FontFamily(
                    androidx.compose.ui.text.font.Font(
                        androidx.compose.ui.res.fontResource(androidx.core.R.font.system_fonts)
                    )
                )
            ),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = androidx.compose.ui.text.font.FontFamily.Default
            )
        ),
        content = content
    )
}
