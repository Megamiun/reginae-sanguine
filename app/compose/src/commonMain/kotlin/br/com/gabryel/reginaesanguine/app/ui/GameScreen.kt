package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.services.PainterLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.components.Grid
import br.com.gabryel.reginaesanguine.app.ui.components.NavigationManager
import br.com.gabryel.reginaesanguine.app.ui.components.RButton
import br.com.gabryel.reginaesanguine.app.ui.fragments.DetailCard
import br.com.gabryel.reginaesanguine.app.ui.fragments.GridPlayableCell
import br.com.gabryel.reginaesanguine.app.ui.fragments.PlayerLanePowerCell
import br.com.gabryel.reginaesanguine.app.ui.fragments.ResultOverlay
import br.com.gabryel.reginaesanguine.app.ui.theme.GridCheckeredOff
import br.com.gabryel.reginaesanguine.app.ui.theme.GridCheckeredOn
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteDark
import br.com.gabryel.reginaesanguine.app.ui.util.getCardSize
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens
import br.com.gabryel.reginaesanguine.app.util.getTransferData
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.State
import br.com.gabryel.reginaesanguine.viewmodel.game.ActiveGameState
import br.com.gabryel.reginaesanguine.viewmodel.game.Awaitable
import br.com.gabryel.reginaesanguine.viewmodel.game.GameViewModel

@Composable
context(painterLoader: PainterLoader, nav: NavigationManager<NavigationScreens>)
fun GameScreen(gameViewModel: GameViewModel) {
    val collectedState by gameViewModel.state.collectAsState()
    val state = collectedState

    if (state !is ActiveGameState) {
        Box(Modifier.fillMaxSize().background(Black.copy(alpha = 0.5f)), Alignment.Center) {
            Text(
                "Waiting for opponent...",
                modifier = Modifier.background(WhiteDark).padding(16.dp),
                color = Black,
            )
        }
        return
    }

    val game = state.game
    val lateralSize = IntSize(1, game.size.height)
    val gridSize = IntSize(game.size.width, game.size.height)
    val cardSize = getCardSize()

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (state.error != null)
            Text(
                state.error.toString(),
                Modifier.fillMaxWidth().align(TopCenter).background(Black),
                textAlign = TextAlign.Center,
                color = Red,
            )

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
                        GridPlayableCell(
                            game,
                            position,
                            cardSize,
                            { gameViewModel.isPlayable(position, it) },
                        ) { cardId -> gameViewModel.play(position, cardId) }
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
            context(PlayerContext.getDefaultFor(game.localPlayerPosition)) {
                game.currentPlayerHand.forEach { card ->
                    val dragAndDrop = remember(card.id) {
                        Modifier.dragAndDropSource { offset -> getTransferData(offset, card.id) }
                    }

                    Box(Modifier.padding(1.dp)) {
                        DetailCard(card, cardSize, dragAndDrop)
                    }
                }
            }
        }

        RButton("Return", Modifier.align(TopStart).offset(15.dp, 15.dp)) { nav.pop() }
        RButton("Skip", Modifier.align(BottomStart).offset(15.dp, (-15).dp)) { gameViewModel.skip() }

        val middleWidth = DpSize(cardSize.width * 8, cardSize.height * 3.1f)

        if (game.getState() is State.Ended)
            ResultOverlay(game, middleWidth)

        // TODO Do this prettily
        if (state is Awaitable)
            Box(Modifier.fillMaxSize().background(Black.copy(alpha = 0.5f)), Alignment.Center) {
                Text(
                    "Waiting for opponent...",
                    modifier = Modifier.background(WhiteDark).padding(16.dp),
                    color = Black,
                )
            }
    }
}

private fun Modifier.boardCell(position: Position): Modifier =
    if ((position.lane + position.column) % 2 == 0)
        cell().background(GridCheckeredOff).padding(2.dp)
    else
        cell().background(GridCheckeredOn).padding(2.dp)

private fun Modifier.playerCell(position: Position): Modifier =
    cell().background(GridCheckeredOn).padding(2.dp)

private fun Modifier.cell(): Modifier = size(getCardSize() + DpSize(1.dp, 1.dp))
