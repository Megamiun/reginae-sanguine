package br.com.gabryel.reginaesanguine.cli

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import br.com.gabryel.reginaesanguine.cli.components.Grid
import br.com.gabryel.reginaesanguine.cli.components.GridConfiguration.Companion.gridWithSize
import br.com.gabryel.reginaesanguine.cli.components.OptionChooser
import br.com.gabryel.reginaesanguine.cli.components.fillWith
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.DOWNWARD
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.LEFTWARD
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.RIGHTWARD
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.UPWARD
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.State.Ended
import br.com.gabryel.reginaesanguine.domain.State.Ended.Tie
import br.com.gabryel.reginaesanguine.domain.State.Ended.Won
import br.com.gabryel.reginaesanguine.domain.State.Ongoing
import br.com.gabryel.reginaesanguine.domain.atColumn
import br.com.gabryel.reginaesanguine.domain.effect.type.EffectWithAffected
import br.com.gabryel.reginaesanguine.domain.effect.type.NoEffect
import br.com.gabryel.reginaesanguine.viewmodel.game.ActiveGameState
import br.com.gabryel.reginaesanguine.viewmodel.game.ChooseAction
import br.com.gabryel.reginaesanguine.viewmodel.game.ChooseCard
import br.com.gabryel.reginaesanguine.viewmodel.game.ChoosePosition
import br.com.gabryel.reginaesanguine.viewmodel.game.GameViewModel
import com.jakewharton.mosaic.LocalTerminalState
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.defaultMinSize
import com.jakewharton.mosaic.layout.fillMaxSize
import com.jakewharton.mosaic.layout.height
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.layout.padding
import com.jakewharton.mosaic.layout.width
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Alignment.Companion.Center
import com.jakewharton.mosaic.ui.Alignment.Companion.CenterHorizontally
import com.jakewharton.mosaic.ui.Arrangement.Absolute.spacedBy
import com.jakewharton.mosaic.ui.Box
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Color.Companion.Black
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Spacer
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle
import com.jakewharton.mosaic.ui.TextStyle.Companion.Bold
import com.jakewharton.mosaic.ui.unit.IntSize

@Composable
fun GameApp(viewModel: GameViewModel) {
    val cellWidth = 9
    val terminal = LocalTerminalState.current

    val collectedState by viewModel.state.collectAsState()
    val state = collectedState

    if (state !is ActiveGameState) {
        Box(Modifier.fillMaxSize(), Center) {
            Text("Waiting for opponent...", color = Black)
        }
        return
    }

    val game = state.game
    val area = game.size
    var selectedChoice by mutableStateOf(0)
    var selectedPosition by mutableStateOf(0 atColumn 0)

    Row(Modifier.defaultMinSize(minHeight = terminal.size.rows - 1).padding(1), spacedBy(1, CenterHorizontally)) {
        Column(
            Modifier.onKeyEvent { event ->
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
                    Box(Modifier.matchParentSize(), contentAlignment = Center) {
                        Text(game.getBaseLaneScoreAt(lane)[LEFT]?.let { "R $it" }.orEmpty())
                    }
                }
                Grid(gridWithSize(IntSize(area.width, area.height), cellSize)) { position ->
                    val cellContent = game.getCellAt(position) ?: return@Grid

                    val textStyle = if (state is ChoosePosition && position == selectedPosition) Bold
                    else TextStyle.Unspecified

                    Column(Modifier.matchParentSize(), horizontalAlignment = CenterHorizontally) {
                        Text(position.describePosition(), textStyle = textStyle)
                        Text(cellContent.describeOwner().orEmpty(), textStyle = textStyle)

                        val cellPower = cellContent.card?.power ?: return@Column
                        Text(cellContent.describeCard(cellPower).orEmpty(), textStyle = textStyle)
                    }
                }
                Grid(gridWithSize(IntSize(1, area.height), cellSize).borderless()) { (lane) ->
                    Box(Modifier.matchParentSize(), contentAlignment = Center) {
                        Text(game.getBaseLaneScoreAt(lane)[RIGHT]?.let { "R $it" }.orEmpty())
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
                is ChooseCard -> {
                    OptionChooser(
                        "Choose card:",
                        game.currentPlayerHand,
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

        Box(Modifier.width(1).height(25).fillWith('#'))
        // TODO Show current card on right
    }
}

fun Card.describe(): String =
    listOfNotNull(
        "$name (Rank $rank, Power $power) - ${increments.map { "(${it.x};${it.y})" }}",
        effect.takeIf { it !is NoEffect }?.let { " - ${it.description}" },
        (effect as? EffectWithAffected)?.affected?.let { affected -> "   - On: ${affected.map { "(${it.x};${it.y})" }}" },
    ).joinToString("\n")

private fun Position.describePosition() = "$lane-$column"

private fun Cell?.describeOwner() = this?.owner?.name

private fun Cell?.describeCard(cellPower: Int) = this
    ?.takeIf { it.owner != null }
    ?.run {
        listOfNotNull(
            cellPower.takeIf { it != 0 }?.let { "P$it" },
            "R$rank",
        ).joinToString(" ")
    }
