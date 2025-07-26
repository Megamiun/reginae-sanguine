package br.com.gabryel.reginaesanguine.cli.components

import androidx.compose.runtime.Composable
import br.com.gabryel.reginaesanguine.domain.Position
import com.jakewharton.mosaic.layout.ContentDrawScope
import com.jakewharton.mosaic.layout.DrawModifier
import com.jakewharton.mosaic.layout.height
import com.jakewharton.mosaic.layout.offset
import com.jakewharton.mosaic.layout.size
import com.jakewharton.mosaic.layout.width
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Box
import com.jakewharton.mosaic.ui.BoxScope
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Spacer
import com.jakewharton.mosaic.ui.unit.IntOffset
import com.jakewharton.mosaic.ui.unit.IntSize

@Composable
fun Grid(
    gridConfiguration: GridConfiguration,
    modifier: Modifier = Modifier,
    getData: @Composable BoxScope.(Position) -> Unit
) {
    val gridSize = gridConfiguration.gridSize
    val cellSize = gridConfiguration.cellSize

    Box(modifier = gridConfiguration.then(modifier)) {
        Column {
            (0 until gridSize.height).forEach { row ->
                Row {
                    (0 until gridSize.width).forEach { col ->
                        Box(modifier = Modifier.height(cellSize.height + 1).width(cellSize.width + 1)) {
                            Box(modifier = Modifier.size(cellSize).offset(1, 1)) {
                                getData(row to col)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(1))
                }
            }
            Spacer(modifier = Modifier.size(1))
        }
    }
}

data class GridConfiguration(
    val gridSize: IntSize,
    val cellSize: IntSize,
    val topStart: Char = '┌',
    val topMiddle: Char = '┬',
    val topEnd: Char = '┐',
    val bottomStart: Char = '└',
    val bottomMiddle: Char = '┴',
    val bottomEnd: Char = '┘',
    val startMiddle: Char = '├',
    val endMiddle: Char = '┤',
    val vertical: Char = '│',
    val horizontal: Char = '─',
    val middle: Char = '┼',
    val drawBorders: Boolean = true
) : DrawModifier {
    companion object {
        fun gridWithSize(gridSize: IntSize, cellSize: IntSize, drawBorders: Boolean = true) =
            GridConfiguration(gridSize, cellSize, drawBorders = drawBorders)
    }

    fun borderless() = copy(drawBorders = false)

    override fun ContentDrawScope.draw() {
        drawContent()

        if (!drawBorders) return

        (0 until gridSize.height).forEach { row ->
            (0 until gridSize.width).forEach { col ->
                drawTopLeftBorder(row, col)
            }

            drawRightBorder(row)
        }

        (0 until gridSize.width).forEach { col ->
            drawLowerBorder(col)
        }

        drawText(getRowStart(gridSize.height), getColStart(gridSize.width), bottomEnd.toString())
    }

    private fun ContentDrawScope.drawTopLeftBorder(row: Int, col: Int) {
        val rowStart = getRowStart(row)
        val colStart = getColStart(col)

        when {
            col == 0 && row == 0 -> drawText(rowStart, colStart, topStart.toString())
            row == 0 -> drawText(rowStart, colStart, topMiddle.toString())
            col == 0 -> drawText(rowStart, colStart, startMiddle.toString())
            else -> drawText(rowStart, colStart, middle.toString())
        }

        drawHorizontal(rowStart, colStart)
        drawVertical(rowStart, colStart)
    }

    private fun ContentDrawScope.drawLowerBorder(col: Int) {
        val rowStart = getRowStart(gridSize.height)
        val colStart = getColStart(col)
        when (col) {
            0 -> drawText(rowStart, colStart, bottomStart.toString())
            else -> drawText(rowStart, colStart, bottomMiddle.toString())
        }

        drawHorizontal(rowStart, colStart)
    }

    private fun ContentDrawScope.drawRightBorder(row: Int) {
        val rowStart = getRowStart(row)
        val colStart = getColStart(gridSize.width)
        when (row) {
            0 -> drawText(rowStart, colStart, topEnd.toString())
            else -> drawText(rowStart, colStart, endMiddle.toString())
        }

        drawVertical(rowStart, colStart)
    }

    private fun ContentDrawScope.drawVertical(rowStart: Int, colStart: Int) {
        drawRect(vertical, topLeft = IntOffset(colStart, rowStart + 1), size = IntSize(1, cellSize.height))
    }

    private fun ContentDrawScope.drawHorizontal(rowStart: Int, colStart: Int) {
        drawRect(horizontal, topLeft = IntOffset(colStart + 1, rowStart), size = IntSize(cellSize.width, 1))
    }

    private fun getColStart(col: Int): Int = col * (cellSize.width + 1)

    private fun getRowStart(row: Int): Int = row * (cellSize.height + 1)
}
