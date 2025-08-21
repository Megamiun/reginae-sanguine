package br.com.gabryel.reginaesanguine.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceEvenly
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
    horizontalArrangement: Arrangement.Horizontal = SpaceEvenly,
    verticalArrangement: Arrangement.Vertical = SpaceEvenly,
    getCell: @Composable (Position) -> Unit = {}
) {
    FlowRow(
        maxItemsInEachRow = gridSize.width,
        maxLines = gridSize.height,
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
    ) {
        repeat(gridSize.height) { row ->
            repeat(gridSize.width) { col ->
                getCell(row atColumn col)
            }
        }
    }
}
