package br.com.gabryel.reginaesanguine.cli

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import br.com.gabryel.reginaesanguine.cli.components.Grid
import br.com.gabryel.reginaesanguine.cli.components.GridConfiguration.Companion.gridWithSize
import br.com.gabryel.reginaesanguine.cli.components.OptionChooser
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.DOWNWARD
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.LEFTWARD
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.RIGHTWARD
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.UPWARD
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.Result
import br.com.gabryel.reginaesanguine.domain.State.Ended
import br.com.gabryel.reginaesanguine.domain.State.Ended.Tie
import br.com.gabryel.reginaesanguine.domain.State.Ended.Won
import br.com.gabryel.reginaesanguine.domain.State.Ongoing
import br.com.gabryel.reginaesanguine.domain.Success
import br.com.gabryel.reginaesanguine.domain.atColumn
import br.com.gabryel.reginaesanguine.viewmodel.GameViewModel
import br.com.gabryel.reginaesanguine.viewmodel.State
import br.com.gabryel.reginaesanguine.viewmodel.State.ChooseAction
import br.com.gabryel.reginaesanguine.viewmodel.State.ChoosePosition
import com.jakewharton.mosaic.LocalTerminalState
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.height
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.layout.width
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Alignment.Companion.Center
import com.jakewharton.mosaic.ui.Alignment.Companion.CenterHorizontally
import com.jakewharton.mosaic.ui.Box
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Spacer
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle
import com.jakewharton.mosaic.ui.TextStyle.Companion.Bold
import com.jakewharton.mosaic.ui.unit.IntSize
import kotlin.test.fail

@Composable
fun GameApp(viewModel: GameViewModel) {
    val cellWidth = 9
    val terminal = LocalTerminalState.current

    val state by viewModel.state.collectAsState()
    val game = state.game
    val player = game.players[game.playerTurn] ?: fail("Couldn´t find player ${game.playerTurn}")

    val area = game.size
    var selectedChoice by mutableStateOf(0)
    var selectedPosition by mutableStateOf(0 atColumn 0)

    Row {
        Column(
            modifier = Modifier
                .width(terminal.size.columns)
                .height(terminal.size.rows - 1)
                .onKeyEvent { event ->
                    if (state is ChoosePosition) {
                        when (event) {
                            KeyEvent("ArrowUp") -> selectedPosition = (selectedPosition + UPWARD).constrainTo(area)
                            KeyEvent("ArrowDown") -> selectedPosition = (selectedPosition + DOWNWARD).constrainTo(area)
                            KeyEvent("ArrowLeft") -> selectedPosition = (selectedPosition + LEFTWARD).constrainTo(area)
                            KeyEvent("ArrowRight") -> selectedPosition = (selectedPosition + RIGHTWARD).constrainTo(area)
                            KeyEvent("Enter") -> viewModel.choosePosition(selectedPosition)
                            else -> return@onKeyEvent false
                        }

                        return@onKeyEvent true
                    }

                    false
                },
        ) {
            Text("Round ${game.round}")
            Spacer(Modifier.height(1))

            Row {
                val cellSize = IntSize(cellWidth, 3)

                Grid(gridWithSize(IntSize(1, area.height), cellSize).borderless()) { (lane) ->
                    Box(modifier = Modifier.matchParentSize(), contentAlignment = Center) {
                        Text(game.getLaneScore(lane)[LEFT]?.let { "R $it" }.orEmpty())
                    }
                }
                Grid(gridWithSize(IntSize(area.width, area.height), cellSize)) { position ->
                    val cellContent = game.getCellAt(position)
                    val cellPower = game.getScoreAt(position) as? Success<Int> ?: return@Grid

                    val textStyle = if (state is ChoosePosition && position == selectedPosition) Bold
                    else TextStyle.Unspecified

                    Column(modifier = Modifier.matchParentSize(), horizontalAlignment = CenterHorizontally) {
                        Text(position.describePosition(), textStyle = textStyle)
                        Text(cellContent.describeOwner().orEmpty(), textStyle = textStyle)
                        Text(cellContent.describeCard(cellPower.value).orEmpty(), textStyle = textStyle)
                    }
                }
                Grid(gridWithSize(IntSize(1, area.height), cellSize).borderless()) { (lane) ->
                    Box(modifier = Modifier.matchParentSize(), contentAlignment = Center) {
                        Text(game.getLaneScore(lane)[RIGHT]?.let { "R $it" }.orEmpty())
                    }
                }
            }

            Spacer(Modifier.height(1))

            Text("Current Score:")
            game.getScores().forEach { player ->
                Text("${player.key} - ${player.value}")
            }

            Spacer(Modifier.height(1))

            if (state.error != null)
                Text(state.error.toString(), color = Color.Red)

            val gameState = game.getState()
            if (gameState is Ended) {
                Column {
                    Text("Game ended!")

                    when (gameState) {
                        is Won -> Text("Player ${gameState.player} won!")
                        is Tie -> Text("Tie!")
                    }
                }
                return@Column
            }

            when (state) {
                is ChooseAction if (game.getState() is Ongoing) -> {
                    OptionChooser(
                        "Choose action:",
                        listOf("PLAY", "SKIP"),
                        selectedIndex = selectedChoice,
                        onIndexChange = { selectedChoice = it },
                    ) {
                        when (it) {
                            "PLAY" -> viewModel.toChooseCard()
                            "SKIP" -> viewModel.skip()
                        }
                        selectedChoice = 0
                    }
                }

                is State.ChooseCard -> {
                    OptionChooser(
                        "Choose card:",
                        player.hand,
                        selectedIndex = selectedChoice,
                        onIndexChange = { selectedChoice = it },
                        describe = Card::describe,
                    ) {
                        viewModel.chooseCard(it.id)
                        selectedChoice = 0
                    }
                }

                else -> {}
            }
        }
        // TODO Explain game on the Right
//        Box(modifier = Modifier.width(1).height(10).fillWith('#'))
    }
}

fun Card.describe(): String = "$name (R $rank, P $power) - $increments"

private fun Position.describePosition() = "$lane-$column"

private fun Result<Cell>.describeOwner() = (this as? Success<Cell>)?.value
    ?.owner?.name

private fun Result<Cell>.describeCard(cellPower: Int) = (this as? Success<Cell>)?.value
    ?.takeIf { it.owner != null }
    ?.run {
        listOfNotNull(
            cellPower.takeIf { it != 0 }?.let { "P$it" },
            "R$rank",
        ).joinToString(" ")
    }
