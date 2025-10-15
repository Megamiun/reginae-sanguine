package br.com.gabryel.reginaesanguine.app.ui.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

private val DEFAULT_CARD_SIZE = 90.dp
private const val WIDTH_PER_HEIGHT_RATIO = 0.83f
private const val HEIGHT_PER_WIDTH_RATIO = 1 / WIDTH_PER_HEIGHT_RATIO

fun getCardSize(height: Dp = DEFAULT_CARD_SIZE, ratio: Float = WIDTH_PER_HEIGHT_RATIO): DpSize {
    val widthValue = height * ratio
    return DpSize(widthValue, height)
}

fun getCardSizeByWidth(width: Dp = DEFAULT_CARD_SIZE, ratio: Float = HEIGHT_PER_WIDTH_RATIO): DpSize {
    val heightValue = width * ratio
    return DpSize(width, heightValue)
}
