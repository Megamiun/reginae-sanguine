package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
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

private val DEFAULT_CARD_SIZE = 90.dp
private val DEFAULT_RATIO = 0.83f

@Composable
context(cardImageLoader: CardImageLoader)
fun BoxScope.GameBoard(gameViewModel: GameViewModel) {
    val state by gameViewModel.state.collectAsState()
    val game = state.game
    val lateralSize = IntSize(1, game.size.height)
    val gridSize = IntSize(game.size.width, game.size.height)
    val cardSize = getCardSize()

    Column(Modifier.align(TopCenter).offset(y = 20.dp).background(WhiteDark).padding(4.dp)) {
        Row(Modifier.border(1.dp, Black)) {
            context(PlayerContext.left) {
                Grid(lateralSize) { position ->
                    Box(Modifier.playerCell(position)) {
                        PlayerLanePowerCell(game, position)
                    }
                }
            }
            Grid(gridSize, modifier = Modifier.border(0.1.dp, WhiteDark)) { position ->
                Box(Modifier.boardCell(position)) {
                    GridPlayableCell(game, position, cardSize) { cardId -> gameViewModel.play(position, cardId) }
                }
            }
            context(PlayerContext.right) {
                Grid(lateralSize) { position ->
                    Box(Modifier.playerCell(position)) {
                        PlayerLanePowerCell(game, position)
                    }
                }
            }
        }
    }

    Row(Modifier.align(BottomCenter), horizontalArrangement = Center) {
        context(PlayerContext.getDefaultFor(game.playerTurn)) {
            game.currentPlayer.hand.forEach { card ->
                val dragAndDrop = Modifier.dragAndDropSource { offset -> getTransferData(offset, card) }

                Box(Modifier.padding(1.dp)) {
                    DetailCard(card, cardSize, dragAndDrop)
                }
            }
        }
    }

    Button(
        gameViewModel::skip,
        Modifier.align(BottomStart).size(70.dp, 30.dp).offset(15.dp, (-15).dp),
    ) {
        Text("SKIP")
    }

    val middleWidth = DpSize(cardSize.width * 8, cardSize.height * 3.1f)

    if (game.getState() is State.Ended)
        ResultOverlay(gameViewModel, middleWidth)
}

expect fun drop(event: DragAndDropEvent, drop: (String) -> Boolean): Boolean

expect fun getTransferData(offset: Offset, card: Card): DragAndDropTransferData

private fun Modifier.boardCell(position: Position): Modifier =
    if ((position.lane + position.column) % 2 == 0)
        cell().background(WhiteLight).padding(2.dp)
    else
        cell().background(PurpleDark).padding(2.dp)

private fun Modifier.playerCell(position: Position): Modifier =
    cell().background(PurpleDark).padding(2.dp)

private fun Modifier.cell(): Modifier = size(getCardSize() + DpSize(1.dp, 1.dp))

private fun getCardSize(height: Dp = DEFAULT_CARD_SIZE, ratio: Float = DEFAULT_RATIO) =
    DpSize(height * ratio, height)
