package br.com.gabryel.reginaesanguine.cli

import br.com.gabryel.reginaesanguine.domain.Action
import br.com.gabryel.reginaesanguine.domain.Action.Skip
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.Result
import br.com.gabryel.reginaesanguine.domain.State
import br.com.gabryel.reginaesanguine.domain.State.Ended.Tie
import br.com.gabryel.reginaesanguine.domain.State.Ended.Won
import br.com.gabryel.reginaesanguine.domain.Success
import br.com.gabryel.reginaesanguine.domain.column
import br.com.gabryel.reginaesanguine.domain.lane
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log
import kotlin.math.roundToInt
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextInt
import kotlin.test.fail

private val coordinatePattern = Regex("""(\d+)[\W+](\d+)""")

private const val CELL_WIDTH = 9

private val EMPTY = "".padEnd(CELL_WIDTH)

fun main() {
    println("Starting new game!")

    val deck = createRandomDeckOfSize(20)

    runGame(deck, deck)
}

private fun runGame(leftDeck: List<Card>, rightDeck: List<Card>) {
    val startTurn = Game.forPlayers(
        Player(deck = leftDeck.shuffled()),
        Player(deck = rightDeck.shuffled()),
    )

    generateSequence(startTurn) { turn ->
        val state = turn.getState()
        if (state is State.Ended) {
            println("\nGame ended!")
            println("\nScore:")

            turn.players.keys.forEach { player ->
                println(" - $player: ") // TODO Get scores
            }

            when (state) {
                is Won -> println("\nPlayer ${state.player} won!")
                is Tie -> println("\nTie!")
            }
            return@generateSequence null
        }

        turn.executeTurn()
    }.last()
}

private fun Game.executeTurn(): Game = generateSequence {
    println("".padEnd(30, '-'))
    val nextPlayer = nextPlayer

    println("Round $round: $nextPlayer turn!")
    println("".padEnd(30, '-'))
    println("\nCurrent board:")

    println(describe())

    val player = players[nextPlayer]
        ?: fail("Player not found")

    val action = readAction(player)

    play(nextPlayer, action)
}.onEach {
    if (it is Failure)
        println("\nInvalid action [$it]")
}.filterIsInstance<Success<Game>>()
    .map { it.value }
    .first()

private fun createRandomDeckOfSize(cards: Int): List<Card> = (1..cards).map {
    val increments = (1..nextInt(4)).map {
        (nextInt(-1, 2) to nextInt(-1, 2)) to 1
    }.distinct().toMap()

    Card(
        "Test Card $it",
        increments,
        nextInt(1, 4),
        3 - floor(log(nextDouble(1.0, 250.0), 10.0)).toInt(),
    )
}

private tailrec fun readAction(player: Player): Action<out String> =
    when (askUserInput("Choose action: ", listOf("SKIP", "PLAY"))) {
        "SKIP" -> Skip
        "PLAY" -> when (val card = askUserInput("Choose card: ", player.hand) { card -> card.describe() }) {
            is Card -> {
                print("\nPlay '${card.name}' at (Lane-Column): ")

                val userPositionInput = readln().trim()
                val userInputValue = coordinatePattern.find(userPositionInput)

                if (userInputValue == null) {
                    println("\nPosition not recognized: $userPositionInput")
                    readAction(player)
                } else {
                    val (lane, col) = userInputValue.groupValues.drop(1).map { it.toInt() }
                    Action.Play(lane to col, card.id)
                }
            }
            else -> readAction(player)
        }
        else -> readAction(player)
    }

private fun <T> askUserInput(question: String, options: List<T>, describeOption: (T) -> String = { it.toString() }): T? {
    println()
    options.forEachIndexed { index, option ->
        println("[$index] ${describeOption(option)}")
    }

    print("\n$question")
    val userInput = readln().trim()
    val userChoice = userInput.toIntOrNull()

    if (userChoice == null || userChoice > options.lastIndex) {
        println("\nInvalid input: $userInput")
        println("Expecting inputs from 0 to ${options.lastIndex}")
        return null
    }

    return options[userChoice]
}

private fun Card.describe(): String = "$name ($ $cost, ⚡ $power) - $increments"

private fun Game.describe(): String {
    val padding = "".cellCentered()
    val middle = "".padEnd((width * (CELL_WIDTH + 1)) - 1, '-')
    val topRow = "${padding}0${middle}0${padding}\n"
    val middleRow = "\n${padding}1${middle}1${padding}\n"
    val bottomRow = "\n${padding}2${middle}2$padding"

    return ((height - 1) downTo 0).joinToString(middleRow, prefix = topRow, postfix = bottomRow) { lane ->
        val positions = (0 until width).map { col -> lane to col }
        val cells = positions.map(::getCellAt)

        listOf(
            listOf(padding) + positions.map { it.describePosition() } + listOf(padding),
            // TODO Add row score
            listOf("⚡ L".cellCentered()) + cells.map { it.describeOwner() } + listOf("⚡ R".cellCentered()),
            listOf(padding) + cells.map { it.describeCard() } + listOf(padding),
        ).joinToString("\n") { it.joinToString("|") }
    }
}

private fun Position.describePosition() = "${lane()}-${column()}".cellCentered()

private fun Result<Cell>.describeOwner() = (this as? Success<Cell>)?.value
    ?.owner?.name
    ?.cellCentered()
    ?: EMPTY

private fun Result<Cell>.describeCard() = (this as? Success<Cell>)?.value
    ?.totalPower?.let { "⚡ $it".cellCentered() }
    ?: EMPTY

private fun Any.cellCentered(width: Int = CELL_WIDTH): String {
    val content = toString()
    val missing = (width - content.length).toDouble()
    val leftPad = "".padStart(floor(missing / 2).roundToInt())
    val rightPad = "".padStart(ceil(missing / 2).roundToInt())

    return "$leftPad$this$rightPad"
}
