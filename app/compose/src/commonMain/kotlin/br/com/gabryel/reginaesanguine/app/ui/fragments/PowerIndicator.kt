package br.com.gabryel.reginaesanguine.app.ui.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.theme.EffectGridBg
import br.com.gabryel.reginaesanguine.app.ui.theme.Yellow
import br.com.gabryel.reginaesanguine.app.ui.theme.YellowAccent
import br.com.gabryel.reginaesanguine.app.ui.theme.createNumbersTextStyle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
context(player: PlayerContext)
fun PlayerPowerIndicator(power: Int, size: Dp, modifier: Modifier = Modifier, accented: Boolean = false) {
    val color = if (accented) player.colorAccent else player.color

    BasePlayerIndicator(color, power, accented, size, listOf(30f, 150f, 270f), modifier)
}

@Composable
fun CardPowerIndicator(power: Int, size: Dp, modifier: Modifier = Modifier, accented: Boolean = false) {
    BasePlayerIndicator(EffectGridBg, power, accented, size, listOf(90f, 210f, 330f), modifier)
}

@Composable
private fun BasePlayerIndicator(
    color: Color,
    power: Int,
    accented: Boolean,
    size: Dp,
    angles: List<Float>,
    modifier: Modifier = Modifier
) {
    val secondaryColor = if (accented) YellowAccent else Yellow
    val borderSize = 1.dp

    val circleModifier = modifier
        .size(size)
        .border(borderSize, secondaryColor, CircleShape)
        .background(secondaryColor, CircleShape)
        .let { if (accented) it.outerGlow(YellowAccent, size) else it }
        .clip(CircleShape)

    val innerSize = size * 0.65f
    val smallCircleModifier = Modifier
        .size(innerSize)
        .clip(CircleShape)
        .background(color)

    Box(circleModifier, contentAlignment = Center) {
        angles.forEach {
            Box(modifier = smallCircleModifier.align(findAlignmentBias(it, 1.4f)))
        }

        LocalDensity.current.run {
            Text(
                power.toString(),
                Modifier.align(Center),
                style = createNumbersTextStyle(innerSize.toSp()),
                color = YellowAccent,
            )
        }
    }
}

@Composable
private fun findAlignmentBias(angle: Float, distance: Float): Alignment {
    val angleRadians = (angle * PI / 180).toFloat()
    return BiasAlignment(cos(angleRadians) * distance, sin(angleRadians) * distance)
}

private fun Modifier.outerGlow(glowColor: Color, diameter: Dp) = drawBehind {
    val radiusPx = diameter.toPx() / 2

    drawCircle(color = glowColor.copy(alpha = 0.08f), radius = radiusPx * 1.3f)
    drawCircle(color = glowColor.copy(alpha = 0.16f), radius = radiusPx * 1.25f)
    drawCircle(color = glowColor.copy(alpha = 0.24f), radius = radiusPx * 1.20f)
    drawCircle(color = glowColor.copy(alpha = 0.32f), radius = radiusPx * 1.15f)
    drawCircle(color = glowColor.copy(alpha = 0.5f), radius = radiusPx * 1.10f)
}
