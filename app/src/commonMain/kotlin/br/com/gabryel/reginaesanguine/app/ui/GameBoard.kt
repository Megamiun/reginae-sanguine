package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.services.CardImageLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.components.Grid
import br.com.gabryel.reginaesanguine.app.ui.theme.PurpleDark
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteDark
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.State
import br.com.gabryel.reginaesanguine.viewmodel.GameViewModel

@Composable
context(cardImageLoader: CardImageLoader)
fun BoxScope.GameBoard(gameViewModel: GameViewModel) {
    val state by gameViewModel.state.collectAsState()
    val game = state.game
    val lateralSize = IntSize(1, game.size.height)
    val gridSize = IntSize(game.size.width, game.size.height)

    Column(Modifier.background(WhiteDark).padding(4.dp)) {
        Row(Modifier.border(1.dp, Black)) {
            context(PlayerContext.left) {
                Grid(lateralSize, cellModifier = Modifier::playerCell) { position ->
                    PlayerLanePowerCell(game, position)
                }
            }
            Grid(
                gridSize,
                modifier = Modifier.border(0.1.dp, WhiteDark),
                cellModifier = Modifier::boardCell,
            ) { position ->
                GridPlayableCell(game, position) { cardId -> gameViewModel.play(position, cardId) }
            }
            context(PlayerContext.right) {
                Grid(lateralSize, cellModifier = Modifier::playerCell) { position ->
                    PlayerLanePowerCell(game, position)
                }
            }
        }
    }

    Row(Modifier.align(BottomCenter).height(60.dp), horizontalArrangement = Arrangement.Center) {
        context(PlayerContext.getDefaultFor(game.playerTurn)) {
            game.currentPlayer.hand.forEach { card ->
                Card(
                    game.playerTurn,
                    card,
                    Modifier.wrapContentSize(unbounded = true)
                        .height(90.dp)
                        .aspectRatio(0.73f)
                        .dragAndDropSource { offset -> getTransferData(offset, card) },
                )
            }
        }
    }

    Button(gameViewModel::skip, Modifier.align(BottomStart).size(70.dp, 30.dp).offset(15.dp, (-15).dp)) {
        Text("SKIP")
    }

    if (game.getState() is State.Ended)
        ResultOverlay(gameViewModel)
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
