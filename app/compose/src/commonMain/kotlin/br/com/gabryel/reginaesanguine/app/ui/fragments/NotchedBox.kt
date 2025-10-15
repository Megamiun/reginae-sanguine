package br.com.gabryel.reginaesanguine.app.ui.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun NotchedBox(
    notchBrush: Brush,
    notchHeight: Dp,
    centerWidth: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier.addUpperNotchedBorder(notchBrush, notchHeight, centerWidth)) {
        content()
    }
}

private fun Modifier.addUpperNotchedBorder(brush: Brush, notchHeight: Dp, centerWidth: Dp = 2.dp) = this
    .clip(SemicircleNotchShape(notchHeight))
    .background(Black)
    .thinningTopBorder(brush, notchHeight, centerWidth)
    .padding(top = notchHeight + centerWidth)

private fun Modifier.thinningTopBorder(
    brush: Brush,
    notchHeight: Dp,
    centerWidth: Dp
): Modifier = drawWithContent {
    drawContent()

    val centerWidthPx = centerWidth.toPx()
    val notchTopHeightPx = -notchHeight.toPx() - centerWidthPx
    val notchBottomHeightPx = notchHeight.toPx() + centerWidthPx

    val borderPath = Path().apply {
        arcTo(
            rect = Rect(left = 0f, top = notchTopHeightPx, right = size.width, bottom = notchBottomHeightPx),
            startAngleDegrees = 180f,
            sweepAngleDegrees = -180f,
            forceMoveTo = true,
        )
        close()
    }

    drawPath(path = borderPath, brush = brush)
}

private class SemicircleNotchShape(private val notchHeight: Dp = 10.dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val notchHeightPx = with(density) { notchHeight.toPx() }

        val path = Path().apply {
            arcTo(
                rect = Rect(left = 0f, top = -notchHeightPx, right = size.width, bottom = notchHeightPx),
                startAngleDegrees = 180f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false,
            )

            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }

        return Outline.Generic(path)
    }
}
