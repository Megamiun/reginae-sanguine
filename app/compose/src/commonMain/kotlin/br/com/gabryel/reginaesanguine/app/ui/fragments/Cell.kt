package br.com.gabryel.reginaesanguine.app.ui.fragments

import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.services.CardImageLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.util.drop
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.Success

private val playerCellInternalModifier = Modifier.fillMaxSize().border(1.dp, Black)

@Composable
context(_: CardImageLoader)
fun GridPlayableCell(game: Game, position: Position, cardSize: DpSize, putCard: (String) -> Boolean) {
    val cellContent = game.getCellAt(position)

    if (cellContent !is Success || cellContent.value.owner == null)
        return

    val cell = cellContent.value

    val card = cell.card
    val owner = cell.owner ?: return

    context(PlayerContext.getDefaultFor(owner)) {
        Box(Modifier, contentAlignment = Center) {
            if (card == null) {
                val dropCallback = remember {
                    object : DragAndDropTarget {
                        override fun onDrop(event: DragAndDropEvent) = drop(event) { putCard(it) }
                    }
                }

                Box(Modifier.size(cardSize), contentAlignment = Center) {
                    CellRankGroup(
                        cell.rank,
                        Modifier.dragAndDropTarget({ _ -> true }, dropCallback),
                        cardSize.width / 2,
                    )
                }
            } else {
                SimpleCard(owner, card, cardSize)
            }
        }
    }
}

@Composable
context(player: PlayerContext)
fun PlayerLanePowerCell(game: Game, position: Position) {
    val playerPower = game.getBaseLaneScoreAt(position.lane)[player.position] ?: 0

    Box(modifier = playerCellInternalModifier, contentAlignment = Center) {
        PlayerPowerIndicator(playerPower, 25.dp, accented = game.getLaneWinner(position.lane) == player.position)
    }
}
