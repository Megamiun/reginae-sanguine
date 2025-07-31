package br.com.gabryel.reginaesanguine.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import br.com.gabryel.reginaesanguine.domain.Position

@Composable
fun Grid(
    gridSize: IntSize,
    modifier: Modifier = Modifier,
    cellModifier: Modifier.(Position) -> Modifier = { Modifier },
    getCell: @Composable BoxScope.(Position) -> Unit
) {
    FlowRow(maxItemsInEachRow = gridSize.width, maxLines = gridSize.height, modifier = modifier) {
        repeat(gridSize.height) { row ->
            repeat(gridSize.width) { col ->
                val position = row to col
                Box(modifier = Modifier.cellModifier(position)) {
                    getCell(position)
                }
            }
        }
    }
}
