package br.com.gabryel.reginaesanguine.app.util

import coil3.Bitmap

actual class Color {
    actual fun red(): Float {
        TODO("Not yet implemented")
    }

    actual fun blue(): Float {
        TODO("Not yet implemented")
    }

    actual fun green(): Float {
        TODO("Not yet implemented")
    }

    actual companion object {
        actual fun rgba(r: Float, g: Float, b: Float): Color {
            TODO("Not yet implemented")
        }
    }
}

actual fun Bitmap.height() = height

actual fun Bitmap.width() = width

actual fun Bitmap.getPColor(x: Int, y: Int): Color {
    TODO("Not yet implemented")
}

actual fun Bitmap.setColor(x: Int, y: Int, color: Color) {
    TODO("Not yet implemented")
}

actual fun getBitmap(width: Int, height: Int): Bitmap {
    TODO("Not yet implemented")
}
