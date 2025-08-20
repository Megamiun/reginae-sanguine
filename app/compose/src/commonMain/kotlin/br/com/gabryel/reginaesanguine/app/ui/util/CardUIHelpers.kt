package br.com.gabryel.reginaesanguine.app.ui.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

private val DEFAULT_CARD_SIZE = 90.dp
private const val DEFAULT_RATIO = 0.83f

fun getCardSize(height: Dp = DEFAULT_CARD_SIZE, ratio: Float = DEFAULT_RATIO) =
    DpSize(height * ratio, height)
