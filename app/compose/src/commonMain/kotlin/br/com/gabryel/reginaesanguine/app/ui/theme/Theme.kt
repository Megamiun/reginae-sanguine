package br.com.gabryel.reginaesanguine.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ColorScheme = darkColorScheme(
    background = Background,
)

@Composable
fun ReginaeSanguineTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = createTypography(),
        content = content,
    )
}
