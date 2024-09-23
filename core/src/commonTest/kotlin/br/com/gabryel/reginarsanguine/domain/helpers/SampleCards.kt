package br.com.gabryel.reginarsanguine.domain.helpers

import br.com.gabryel.reginarsanguine.domain.Card
import br.com.gabryel.reginarsanguine.domain.Position

object SampleCards {
    val SECURITY_OFFICER =
        Card(
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

    fun cardOf(
        increments: Map<Position, Int> = emptyMap(),
        value: Int = 0,
        price: Int = 0,
    ) = Card(increments, value, price)
}
