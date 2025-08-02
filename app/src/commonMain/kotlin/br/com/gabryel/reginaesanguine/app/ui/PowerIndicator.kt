package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.theme.Yellow
import br.com.gabryel.reginaesanguine.app.ui.theme.YellowAccent
import br.com.gabryel.reginaesanguine.app.ui.theme.createTextStyle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
context(player: PlayerContext)
fun PowerIndicator(power: Int, accented: Boolean, multiplier: Float = 1f) {
    val size = 35.dp * multiplier
    val circleModifier = Modifier
        .size(size)
        .let { if (accented) it.outerGlow(YellowAccent, size) else it }
        .clip(CircleShape)
        .background(if (accented) YellowAccent else Yellow)

    val smallCircleModifier = Modifier
        .size(23.dp * multiplier)
        .clip(CircleShape)
        .background(if (accented) player.colorAccent else player.color)

    Box(circleModifier, contentAlignment = Center) {
        listOf(30f, 150f, 270f).forEach {
            Box(modifier = smallCircleModifier.align(findAlignmentBias(it, 0.85f)))
        }

        Text(power.toString(), style = createTextStyle(multiplier))
    }
}

private fun findAlignmentBias(angle: Float, distance: Float): Alignment {
    val angleRadians = (angle * PI / 180).toFloat()
    return BiasAlignment(cos(angleRadians) * distance, sin(angleRadians) * distance)
}

private fun Modifier.outerGlow(glowColor: Color, diameter: Dp) = drawBehind {
    val radiusPx = diameter.toPx() / 2
    drawCircle(color = glowColor.copy(alpha = 0.1f), radius = radiusPx * 1.25f)
    drawCircle(color = glowColor.copy(alpha = 0.2f), radius = radiusPx * 1.20f)
    drawCircle(color = glowColor.copy(alpha = 0.3f), radius = radiusPx * 1.15f)
    drawCircle(color = glowColor.copy(alpha = 0.5f), radius = radiusPx * 1.10f)
    drawCircle(color = glowColor.copy(alpha = 0.7f), radius = radiusPx * 0.9f)
}
