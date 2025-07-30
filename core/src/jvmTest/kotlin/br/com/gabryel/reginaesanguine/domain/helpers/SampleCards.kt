package br.com.gabryel.reginaesanguine.domain.helpers

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.effect.Effect

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
        affected: List<Position> = emptyList(),
        effect: Effect? = null,
    ) = Card(id, name, increments, value, rank, affected, effect)
}
