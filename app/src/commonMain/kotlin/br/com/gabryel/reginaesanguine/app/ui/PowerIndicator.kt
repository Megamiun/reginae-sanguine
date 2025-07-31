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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.ui.theme.Yellow
import br.com.gabryel.reginaesanguine.app.ui.theme.createTextStyle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PowerIndicator(power: Int, color: Color, multiplier: Float = 1f) {
    val circleModifier = Modifier.size(35.dp * multiplier).clip(CircleShape).background(Yellow)
    val smallCircleModifier = Modifier.size(23.dp * multiplier).clip(CircleShape).background(color)

    Box(modifier = circleModifier, contentAlignment = Center) {
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
