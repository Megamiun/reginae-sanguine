package br.com.gabryel.reginaesanguine.cli

import br.com.gabryel.reginaesanguine.domain.Action
import br.com.gabryel.reginaesanguine.domain.Action.Skip
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.State
import br.com.gabryel.reginaesanguine.domain.State.Ended.Tie
import br.com.gabryel.reginaesanguine.domain.State.Ended.Won
import br.com.gabryel.reginaesanguine.domain.Success
import kotlin.math.floor
import kotlin.math.log
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextInt
import kotlin.test.fail

private val coordinatePattern = Regex("""(\d+)[\W+](\d+)""")

fun main() {
    println("Starting new game!")

    val deck = (1..20).map {
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

    generateSequence(Game.forPlayers(Player(deck = deck.shuffled()), Player(deck = deck.shuffled()))) { turn ->
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

        generateSequence {
            println("".padEnd(30, '-'))
            val nextPlayer = turn.nextPlayer

            println("Round ${turn.round}: $nextPlayer turn!")
            println("".padEnd(30, '-'))
            println("\nCurrent board:")

            println(turn.describe())

            val player = turn.players[nextPlayer]
                ?: fail("Player not found")

            val action = readAction(player)

            turn.play(nextPlayer, action)
        }.onEach {
            if (it is Failure)
                println("\nInvalid action [$it]")
        }.filterIsInstance<Success<Game>>()
            .map { it.value }
            .first()
    }.last()
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

private fun Game.describe(): String = "board" // TODO print board
