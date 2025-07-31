package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.services.CardImageLoader
import br.com.gabryel.reginaesanguine.app.ui.components.Grid
import br.com.gabryel.reginaesanguine.app.ui.theme.PurpleDark
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteDark
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.app.util.Logger
import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.Success

@Composable
context(cardImageLoader: CardImageLoader)
fun BoxScope.GameBoard(startGame: Game) {
    val logger = remember { Logger("GameBoard") }
    val gameState = remember { mutableStateOf(startGame) }
    val lateralSize = IntSize(1, gameState.value.size.height)
    val gridSize = IntSize(gameState.value.size.width, gameState.value.size.height)

    Column(modifier = Modifier.background(WhiteDark).padding(4.dp)) {
        Row(modifier = Modifier.border(1.dp, Black)) {
            Grid(lateralSize, cellModifier = Modifier::playerCell) { position ->
                PlayerLanePowerCell(gameState.value, position, LEFT)
            }
            Grid(
                gridSize,
                modifier = Modifier.border(0.1.dp, WhiteDark),
                cellModifier = Modifier::boardCell,
            ) { position ->
                GridPlayableCell(gameState.value, position) { cardId ->
                    when (
                        val newGameState = gameState.value.play(
                            gameState.value.nextPlayerPosition,
                            Play(position, cardId),
                        )
                    ) {
                        is Success<Game> -> {
                            gameState.value = newGameState.value
                            true
                        }
                        else -> {
                            logger.error("$newGameState")
                            false
                        }
                    }
                }
            }
            Grid(lateralSize, cellModifier = Modifier::playerCell) { position ->
                PlayerLanePowerCell(gameState.value, position, RIGHT)
            }
        }
    }

    Row(modifier = Modifier.align(BottomCenter).fillMaxWidth().height(60.dp), horizontalArrangement = Arrangement.Center) {
        gameState.value.nextPlayer.hand.forEachIndexed { index, card ->
            Card(
                gameState.value.nextPlayerPosition,
                card,
                Modifier.wrapContentSize(unbounded = true)
                    .height(90.dp)
                    .aspectRatio(0.73f)
                    .dragAndDropSource { offset -> getTransferData(offset, card) },
            )
        }
    }
}

expect fun drop(event: DragAndDropEvent, drop: (String) -> Boolean): Boolean

expect fun getTransferData(offset: Offset, card: Card): DragAndDropTransferData

private fun Modifier.boardCell(position: Position): Modifier =
    if ((position.lane + position.column) % 2 == 0) {
        cellSize().background(WhiteLight)
    } else {
        cellSize().background(PurpleDark)
    }

private fun Modifier.playerCell(position: Position): Modifier =
    cellSize()
        .background(PurpleDark)
        .padding(1.25.dp)

private fun Modifier.cellSize(): Modifier =
    width(90.dp).aspectRatio(0.73f)
