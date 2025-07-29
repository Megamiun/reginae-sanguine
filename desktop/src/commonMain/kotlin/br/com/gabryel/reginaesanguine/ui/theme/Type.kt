package br.com.gabryel.reginaesanguine.ui.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.ExtraBold
import androidx.compose.ui.text.font.FontWeight.Companion.Light
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.sp
import br.com.gabryel.desktop.generated.resources.Res
import br.com.gabryel.desktop.generated.resources.mono
import org.jetbrains.compose.resources.Font

@Composable
private fun MonoFont() = FontFamily(
    Font(Res.font.mono, Normal),
    Font(Res.font.mono, Light),
    Font(Res.font.mono, SemiBold),
    Font(Res.font.mono, ExtraBold),
)

// class FonResolver: FontFamily.Resolver {
//    override suspend fun preload(fontFamily: FontFamily) {
//        fontFamily.
//    }
//
//    override fun resolve(
//        fontFamily: FontFamily?,
//        fontWeight: FontWeight,
//        fontStyle: FontStyle,
//        fontSynthesis: FontSynthesis
//    ): State<Any> {
//    }
// }

@Composable
fun Typography() = Typography(body1 = AppTextStyle())

@Composable
fun AppTextStyle(multiplier: Float = 1f) = TextStyle(
    fontFamily = MonoFont(),
    fontWeight = Normal,
    fontSize = defaultTextSize(multiplier),
    lineHeight = 24.sp * multiplier,
    letterSpacing = 0.5.sp,
    color = WhiteLight,
)

fun defaultTextSize(multiplier: Float = 1f) = 16.sp * multiplier
