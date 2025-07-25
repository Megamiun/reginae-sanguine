package br.com.gabryel.reginaesanguine.domain.helpers

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Effect
import br.com.gabryel.reginaesanguine.domain.Position

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

    fun cardOf(
        name: String = "Unnamed",
        increments: Map<Position, Int> = emptyMap(),
        value: Int = 1,
        rank: Int = 1,
        effectDisplacements: List<Position> = emptyList(),
        effects: List<Effect> = emptyList(),
    ) = Card(name, increments, value, rank, effectDisplacements, effects)
}
