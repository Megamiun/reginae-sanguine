package br.com.gabryel.reginaesanguine.domain.helpers

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Effect
import br.com.gabryel.reginaesanguine.domain.Position

object SampleCards {
    val SECURITY_OFFICER =
        Card(
            "001",
            "Security Officer",
            setOf(
                1 to 0,
                0 to 1,
                -1 to 0,
                0 to -1,
            ),
            1,
            1,
        )

    val RIOT_TROOPER =
        Card(
            "002",
            "Riot Trooper",
            setOf(
                1 to 0,
                2 to 0,
                0 to 1,
                -1 to 0,
                -2 to 0,
            ),
            3,
            2,
        )

    fun cardOf(
        id: String = "Unnamed",
        name: String = id,
        increments: Set<Position> = emptySet(),
        value: Int = 1,
        rank: Int = 1,
        effectDisplacements: List<Position> = emptyList(),
        effects: List<Effect> = emptyList(),
    ) = Card(id, name, increments, value, rank, effectDisplacements, effects)
}
