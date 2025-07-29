package br.com.gabryel.reginaesanguine.util

import br.com.gabryel.reginaesanguine.domain.Card
import kotlin.math.floor
import kotlin.math.log
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextInt

fun createRandomDeckOfSize(cards: Int): List<Card> = (1..cards).map {
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
