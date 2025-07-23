package br.com.gabryel.reginaesanguine.cli

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Player
import kotlin.math.floor
import kotlin.math.log
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextInt
import kotlin.test.fail

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
            3 - floor(log(nextDouble(10.0, 1000.0), 10.0)).toInt(),
        )
    }

    generateSequence(Game.forPlayers(Player(deck = deck.shuffled()), Player(deck = deck.shuffled()))) { turn ->
        println("\nRound ${turn.round}: ${turn.nextPlayer} turn!")
        println("\nCurrent board:")

        println(turn.describe())

        println("\nCurrent hand:")

        val player = turn.players[turn.nextPlayer]
            ?: fail("Player not found")

        println("[0] WAIT")
        player.hand.forEachIndexed { index, card ->
            println("[${index + 1}] ${card.describe()}")
        }

        print("\nChoose action: ")
        val action = readln()

        turn
    }.last()
}

private fun Card.describe(): String = "$name (⚡ $power, $ $cost) - $increments"

 private fun Game.describe(): String = "board" // TODO print board
