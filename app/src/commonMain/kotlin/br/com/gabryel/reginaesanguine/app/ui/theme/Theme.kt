package br.com.gabryel.reginaesanguine.app.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val ColorScheme = darkColors(
    background = PurpleLight,
)

@Composable
fun ReginaeSanguineTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = ColorScheme,
        typography = createTypography(),
        content = content,
    )
}
