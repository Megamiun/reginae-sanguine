package br.com.gabryel.reginaesanguine.app.ui.components

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.atColumn

@Composable
fun Grid(
    gridSize: IntSize,
    modifier: Modifier = Modifier,
    getCell: @Composable (Position) -> Unit = {}
) {
    FlowRow(maxItemsInEachRow = gridSize.width, maxLines = gridSize.height, modifier = modifier) {
        repeat(gridSize.height) { row ->
            repeat(gridSize.width) { col ->
                getCell(row atColumn col)
            }
        }
    }
}
