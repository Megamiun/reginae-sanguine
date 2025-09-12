package br.com.gabryel.reginaesanguine.app.util

import android.graphics.Bitmap.Config.RGB_565
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import coil3.Bitmap

typealias AndroidColor = android.graphics.Color

actual class Color(val color: AndroidColor) {
    actual companion object {
        actual fun rgba(r: Float, g: Float, b: Float) = Color(AndroidColor.valueOf(r, g, b))
    }

    actual fun red() = color.red()

    actual fun blue() = color.blue()

    actual fun green() = color.green()
}

actual fun Bitmap.width() = width

actual fun Bitmap.height() = height

actual fun Bitmap.getPColor(x: Int, y: Int) = Color(getColor(x, y))

actual fun Bitmap.setColor(x: Int, y: Int, color: Color) {
    set(x, y, color.color.toArgb())
}

actual fun getBitmap(width: Int, height: Int) = createBitmap(width, height, RGB_565)
