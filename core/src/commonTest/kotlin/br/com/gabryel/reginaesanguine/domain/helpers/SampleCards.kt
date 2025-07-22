package br.com.gabryel.reginaesanguine.domain.helpers

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.RaisePower

object SampleCards {
    val SECURITY_OFFICER =
        Card(
            "Security Officer",
            mapOf(
                1 to 0 to 1,
                0 to 1 to 1,
                -1 to 0 to 1,
                0 to -1 to 1,
            ),
            1,
            1,
        )

    val RIOT_TROOPER =
        Card(
            "Riot Trooper",
            mapOf(
                1 to 0 to 1,
                2 to 0 to 1,
                0 to 1 to 1,
                -1 to 0 to 1,
                -2 to 0 to 1,
            ),
            3,
            2,
        )

    val CRYSTALLINE_CRAB =
        Card(
            "Crystalline Creab",
            mapOf(
                1 to 0 to 1,
                -1 to 0 to 1,
                0 to -1 to 1,
            ),
            1,
            1,
            listOf(RaisePower(listOf(0 to -1), 2))
        )

    fun cardOf(
        name: String = "Unnamed",
        increments: Map<Position, Int> = emptyMap(),
        value: Int = 0,
        price: Int = 0,
    ) = Card(name, increments, value, price)
}
