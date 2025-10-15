package br.com.gabryel.reginaesanguine.app.ui.decorations

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.app.ui.util.mirror
import br.com.gabryel.reginaesanguine.app.ui.util.squared
import br.com.gabryel.reginaesanguine.app.ui.util.times

fun Modifier.addFancyCorners() = drawBehind {
    val padding = 6f
    drawCorner(Offset(padding, padding), 1f, 1f, 0f)
    drawCorner(Offset(size.width - padding, padding), -1f, 1f, 90f)
    drawCorner(Offset(padding, size.height - padding), 1f, -1f, 270f)
    drawCorner(Offset(size.width - padding, size.height - padding), -1f, -1f, 180f)
}

private fun DrawScope.drawCorner(origin: Offset, hDir: Float, vDir: Float, arcAngle: Float) {
    val color = WhiteLight
    val strokeWidth = 1.8f
    val dirOffset = Offset(hDir, vDir)

    fun drawMirrored(offset: Offset, direction: Offset) {
        val startOffset = origin + (offset * dirOffset)
        val endOffset = startOffset + (direction * dirOffset)

        drawLine(color, startOffset, endOffset, strokeWidth)
        drawLine(color, origin + (offset.mirror() * dirOffset), origin + ((offset + direction).mirror() * dirOffset), strokeWidth)
    }

    // Outer Lines
    val outerBorderRadius = 8f
    val outerBorderSize = 30f

    drawMirrored(Offset(0f, outerBorderRadius), Offset(0f, outerBorderSize))

    drawArc(
        color,
        arcAngle,
        90f,
        false,
        origin - Offset(outerBorderRadius, outerBorderRadius),
        Size.Companion.squared(outerBorderRadius * 2),
        style = Stroke(strokeWidth),
    )

    // Inner Lines
    val innerBorderRadius = outerBorderRadius + (strokeWidth * 2)
    val innerBorderStart = outerBorderRadius + outerBorderSize + (strokeWidth * 2)
    val innerBorderSize = 30f

    drawMirrored(Offset(0f, innerBorderStart), Offset(0f, innerBorderSize))
    drawMirrored(Offset(0f, innerBorderStart), Offset(strokeWidth * 2, 0f))
    drawMirrored(Offset(strokeWidth * 2, innerBorderStart), Offset(0f, -outerBorderSize))

    drawArc(
        color,
        arcAngle + 15,
        60f,
        false,
        origin - Offset(innerBorderRadius, innerBorderRadius),
        Size.squared(innerBorderRadius * 2),
        style = Stroke(strokeWidth),
    )
}
