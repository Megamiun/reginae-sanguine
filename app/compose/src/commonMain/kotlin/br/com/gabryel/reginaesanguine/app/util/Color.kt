package br.com.gabryel.reginaesanguine.app.util

import coil3.Bitmap

// TODO Challenge: Implement a nonAndroid version
expect class Color {
    companion object {
        fun rgba(r: Float, g: Float, b: Float): Color
    }

    fun red(): Float

    fun blue(): Float

    fun green(): Float
}

expect fun Bitmap.width(): Int

expect fun Bitmap.height(): Int

// TODO Get a better name
expect fun Bitmap.getPColor(x: Int, y: Int): Color

expect fun Bitmap.setColor(x: Int, y: Int, color: Color)

expect fun getBitmap(width: Int, height: Int): Bitmap
