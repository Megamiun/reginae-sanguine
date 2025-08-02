package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.services.CardImageLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.Success

private val playerCellInternalModifier = Modifier.fillMaxSize().border(1.dp, Black)

@Composable
context(_: CardImageLoader)
fun BoxScope.GridPlayableCell(game: Game, position: Position, putCard: (String) -> Boolean) {
    val cellContent = game.getCellAt(position)

    if (cellContent !is Success || cellContent.value.owner == null)
        return

    val cell = cellContent.value

    val card = cell.card
    val owner = cell.owner ?: return

    context(PlayerContext.getDefaultFor(owner)) {
        if (card == null) {
            val dropCallback = remember {
                object : DragAndDropTarget {
                    override fun onDrop(event: DragAndDropEvent) = drop(event) { putCard(it) }
                }
            }

            RankGroup(
                cell.rank,
                10f,
                Modifier.matchParentSize().dragAndDropTarget({ _ -> true }, dropCallback),
            )
            return
        }

        Card(owner, card, Modifier.matchParentSize())
    }
}

@Composable
context(player: PlayerContext)
fun PlayerLanePowerCell(game: Game, position: Position) {
    val playerPower = game.getLaneScore(position.lane)[player.position] ?: 0

    Box(modifier = playerCellInternalModifier, contentAlignment = Center) {
        PowerIndicator(playerPower, game.getLaneWinner(position.lane) == player.position)
    }
}
