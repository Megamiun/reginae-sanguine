package br.com.gabryel.reginaesanguine.cli

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.Result
import br.com.gabryel.reginaesanguine.domain.State
import br.com.gabryel.reginaesanguine.domain.Success
import br.com.gabryel.reginaesanguine.domain.column
import br.com.gabryel.reginaesanguine.domain.lane
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class GameRenderer {
    private val cellWidth = 9
    private val empty = "".padEnd(cellWidth)

    // ANSI color codes
    private val red = "\u001B[31m"
    private val reset = "\u001B[0m"

    private fun String.inRed() = "$red$this$reset"

    fun renderBoard(game: Game) {
        println("\nCurrent board:")

        val padding = "".cellCentered()
        val cellLine = "".padEnd(cellWidth, '─')
        val topRow = (0 until game.width).joinToString("┬", prefix = "$padding┌", postfix = "┐$padding\n") { cellLine }
        val middleRow = (0 until game.width).joinToString("┼", prefix = "\n$padding├", postfix = "┤$padding\n") { cellLine }
        val bottomRow = (0 until game.width).joinToString("┴", prefix = "\n$padding└", postfix = "┘$padding") { cellLine }

        val content = ((game.height - 1) downTo 0).joinToString(middleRow, prefix = topRow, postfix = bottomRow) { lane ->
            val positions = (0 until game.width).map { col -> lane to col }
            val cells = positions.map(game::getCellAt)
            val scores = game.getLaneScore(lane)

            listOf(
                listOf(padding) + positions.map { it.describePosition() } + listOf(padding),
                listOf("⚡ ${scores[LEFT]}".cellCentered()) + cells.map { it.describeOwner() } + listOf("⚡ ${scores[RIGHT]}".cellCentered()),
                listOf(padding) + cells.map { it.describeCard() } + listOf(padding),
            ).joinToString("\n") { it.joinToString("│") }
        }

        println(content)
    }

    fun renderScore(game: Game) {
        println("\nCurrent score:")
        val scores = game.getScores()
        game.players.keys.forEach { player ->
            println(" - $player: ⚡ ${scores[player]}")
        }
    }

    fun renderGameHeader(round: Int, nextPlayer: String) {
        println("".padEnd(30, '─'))
        println("Round $round: $nextPlayer turn!")
        println("".padEnd(30, '─'))
    }

    fun renderGameEnd(state: State.Ended) {
        println("\nGame ended!")

        when (state) {
            is State.Ended.Won -> println("\nPlayer ${state.player} won!")
            is State.Ended.Tie -> println("\nTie!")
        }
    }

    fun renderActionError(failure: Failure) {
        println("\nInvalid action [$failure]".inRed())
    }

    fun renderPositionError(position: String) {
        println("\nPosition not recognized: $position".inRed())
    }

    fun <T> renderInputOptions(options: List<T>, describeOption: (T) -> String) {
        println()
        options.forEachIndexed { index, option ->
            println("[$index] ${describeOption(option)}")
        }
    }

    fun renderInputPrompt(question: String) {
        print("\n$question")
    }

    fun renderInputValidationError(input: String, maxIndex: Int) {
        println("\nInvalid input: $input".inRed())
        println("Expecting inputs from 0 to $maxIndex".inRed())
    }

    fun describeCard(card: Card): String = "${card.name} ($ ${card.cost}, ⚡ ${card.power}) - ${card.increments}"

    private fun Position.describePosition() = "${lane()}-${column()}".cellCentered()

    private fun Result<Cell>.describeOwner() = (this as? Success<Cell>)?.value
        ?.owner?.name
        ?.cellCentered()
        ?: empty

    private fun Result<Cell>.describeCard() = (this as? Success<Cell>)?.value
        ?.takeIf { it.owner != null }
        ?.run {
            listOfNotNull(
                totalPower?.let { "⚡$it" },
                "$$pins",
            ).joinToString(" ").cellCentered()
        } ?: empty

    private fun Any.cellCentered(width: Int = cellWidth): String {
        val content = toString()
        val missing = (width - content.length).toDouble()
        val leftPad = "".padStart(floor(missing / 2).roundToInt())
        val rightPad = "".padStart(ceil(missing / 2).roundToInt())

        return "$leftPad$this$rightPad"
    }
}
