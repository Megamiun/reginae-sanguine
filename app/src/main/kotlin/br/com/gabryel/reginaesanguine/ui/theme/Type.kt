package br.com.gabryel.reginaesanguine.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.com.gabryel.reginaesanguine.R

private val monoFont = FontFamily(
    Font(R.font.mono, FontWeight.Normal),
    Font(R.font.mono, FontWeight.Light),
    Font(R.font.mono, FontWeight.SemiBold),
    Font(R.font.mono, FontWeight.ExtraBold),
)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = monoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
)
