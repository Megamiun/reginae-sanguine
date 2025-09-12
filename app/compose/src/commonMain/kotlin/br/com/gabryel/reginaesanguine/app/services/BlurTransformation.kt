package br.com.gabryel.reginaesanguine.app.services

import br.com.gabryel.reginaesanguine.app.util.Color
import br.com.gabryel.reginaesanguine.app.util.getBitmap
import br.com.gabryel.reginaesanguine.app.util.getPColor
import br.com.gabryel.reginaesanguine.app.util.height
import br.com.gabryel.reginaesanguine.app.util.setColor
import br.com.gabryel.reginaesanguine.app.util.width
import coil3.Bitmap
import coil3.size.Size
import coil3.transform.Transformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BlurTransformation(val radius: Int) : Transformation() {
    override val cacheKey: String = "${this::class.simpleName}-$radius"

    // TODO This is very slow currently. Check how to make it more performant... Or just load it blurred already D:
    override suspend fun transform(input: Bitmap, size: Size): Bitmap = withContext(Dispatchers.Unconfined) {
        val result = getBitmap(input.width(), input.height())

        val width = 0 until result.width()
        val height = 0 until result.height()

        val colorCache = Array(result.width()) { x ->
            Array(result.height()) { y ->
                input.getPColor(x, y)
            }
        }

        for (x in width) {
            for (y in height) {
                result.setPixel(x, y, width, height, colorCache)
            }
        }

        return@withContext result
    }

    private fun Bitmap.setPixel(x: Int, y: Int, width: IntRange, height: IntRange, input: Array<Array<Color>>) {
        val xs = ((x - radius)..(x + radius)).filter { it in width }
        val ys = ((y - radius)..(y + radius)).filter { it in height }

        val all = xs.flatMap { refX -> ys.map { refY -> input[refX][refY] } }

        val r = all.map { it.red() }.average().toFloat()
        val g = all.map { it.green() }.average().toFloat()
        val b = all.map { it.blue() }.average().toFloat()

        setColor(x, y, Color.rgba(r, g, b))
    }
}
