package br.com.gabryel.reginaesanguine.app.ui.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

fun Size.Companion.squared(size: Float) = Size(size, size)

fun Offset.Companion.squared(size: Float) = Offset(size, size)

fun Offset.mirror(): Offset = Offset(y, x)

operator fun Offset.times(offset: Offset) = Offset(x * offset.x, y * offset.y)
