package br.com.gabryel.reginaesanguine.cli

import br.com.gabryel.reginaesanguine.domain.Action
import br.com.gabryel.reginaesanguine.domain.Action.Skip
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.State
import br.com.gabryel.reginaesanguine.domain.Success
import kotlin.math.floor
import kotlin.math.log
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextInt
import kotlin.test.fail

private val coordinatePattern = Regex("""(\d+)[\W+](\d+)""")

fun main() {
    val deck = createRandomDeckOfSize(20)
    val renderer = GameRenderer()

    runGame(deck, deck, renderer)
}

private fun runGame(leftDeck: List<Card>, rightDeck: List<Card>, renderer: GameRenderer) {
    val startTurn = Game.forPlayers(
        Player(deck = leftDeck.shuffled()),
        Player(deck = rightDeck.shuffled()),
    )

    generateSequence(startTurn) { turn ->
        val nextPlayer = turn.nextPlayer
        renderer.renderGameHeader(turn.round, nextPlayer.toString())

        renderer.renderBoard(turn)
        renderer.renderScore(turn)

        val state = turn.getState()
        if (state is State.Ended) {
            renderer.renderGameEnd(state)
            return@generateSequence null
        }

        turn.executeTurn(renderer)
    }.last()
}

private fun Game.executeTurn(renderer: GameRenderer): Game = generateSequence {
    val player = players[nextPlayer] ?: fail("Player not found")
    val action = readAction(player, renderer)

    play(nextPlayer, action)
}.onEach {
    if (it is Failure) renderer.renderActionError(it)
}.filterIsInstance<Success<Game>>()
    .map { it.value }
    .first()

private fun createRandomDeckOfSize(cards: Int): List<Card> = (1..cards).map {
    val increments = (0..1 + nextInt(4)).map {
        (nextInt(-1, 2) to nextInt(-1, 2)) to 1
    }.distinct().toMap()

    Card(
        "Test Card $it",
        increments,
        nextInt(1, 4),
        3 - floor(log(nextDouble(1.0, 250.0), 10.0)).toInt(),
    )
}

private tailrec fun readAction(player: Player, renderer: GameRenderer): Action<out String> =
    when (askUserInput("Choose action: ", listOf("SKIP", "PLAY"), renderer)) {
        "SKIP" -> Skip
        "PLAY" -> when (val card = askUserInput("Choose card: ", player.hand, renderer) { renderer.describeCard(it) }) {
            is Card -> {
                print("\nPlay '${card.name}' at (Lane-Column): ")

                val userPositionInput = readln().trim()
                val userInputValue = coordinatePattern.find(userPositionInput)

                if (userInputValue == null) {
                    renderer.renderPositionError(userPositionInput)
                    readAction(player, renderer)
                } else {
                    val (lane, col) = userInputValue.groupValues.drop(1).map { it.toInt() }
                    Action.Play(lane to col, card.id)
                }
            }
            else -> readAction(player, renderer)
        }
        else -> readAction(player, renderer)
    }

private fun <T> askUserInput(
    question: String,
    options: List<T>,
    renderer: GameRenderer,
    describeOption: (T) -> String = { it.toString() }
): T? {
    renderer.renderInputOptions(options, describeOption)
    renderer.renderInputPrompt(question)

    val userInput = readln().trim()
    val userChoice = userInput.toIntOrNull()

    if (userChoice == null || userChoice > options.lastIndex) {
        renderer.renderInputValidationError(userInput, options.lastIndex)
        return null
    }

    return options[userChoice]
}
