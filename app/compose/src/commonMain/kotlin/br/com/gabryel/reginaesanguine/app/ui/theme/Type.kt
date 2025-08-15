package br.com.gabryel.reginaesanguine.app.ui.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.ExtraBold
import androidx.compose.ui.text.font.FontWeight.Companion.Light
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import br.com.gabryel.reginaesanguine.app.AnglicanText
import br.com.gabryel.reginaesanguine.app.GillSans_Bold
import br.com.gabryel.reginaesanguine.app.GillSans_Light
import br.com.gabryel.reginaesanguine.app.GillSans_Medium
import br.com.gabryel.reginaesanguine.app.Res
import org.jetbrains.compose.resources.Font

@Composable
private fun createNumbersFontFamily() = FontFamily(
    Font(Res.font.AnglicanText, Normal),
)

@Composable
fun createNumbersTextStyle(fontSize: TextUnit) = TextStyle(
    fontFamily = createNumbersFontFamily(),
    fontWeight = Bold,
    fontSize = fontSize,
    color = WhiteLight,
)

@Composable
private fun createFontFamily() = FontFamily(
    Font(Res.font.GillSans_Medium, Normal),
    Font(Res.font.GillSans_Light, Light),
    Font(Res.font.GillSans_Bold, SemiBold),
    Font(Res.font.GillSans_Bold, ExtraBold),
)

@Composable
fun createTypography() = Typography(body1 = createTextStyle())

@Composable
fun createTextStyle(multiplier: Float = 1f) = TextStyle(
    fontFamily = createFontFamily(),
    fontWeight = Normal,
    fontSize = defaultTextSize(multiplier),
    letterSpacing = 0.5.sp,
    color = WhiteLight,
)

fun defaultTextSize(multiplier: Float = 1f) = 16.sp * multiplier
