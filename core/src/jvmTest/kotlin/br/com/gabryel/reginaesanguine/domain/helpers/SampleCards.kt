package br.com.gabryel.reginaesanguine.domain.helpers

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Displacement
import br.com.gabryel.reginaesanguine.domain.effect.Effect

object SampleCards {
    val SECURITY_OFFICER =
        Card(
            "001",
            "Security Officer",
            setOf(
                Displacement(1, 0),
                Displacement(0, 1),
                Displacement(-1, 0),
                Displacement(0, -1),
            ),
            1,
            1,
        )

    val RIOT_TROOPER =
        Card(
            "002",
            "Riot Trooper",
            setOf(
                Displacement(1, 0),
                Displacement(2, 0),
                Displacement(0, 1),
                Displacement(-1, 0),
                Displacement(-2, 0),
            ),
            3,
            2,
        )

    fun cardOf(
        id: String = "Unnamed",
        name: String = id,
        increments: Set<Displacement> = emptySet(),
        value: Int = 1,
        rank: Int = 1,
        affected: Set<Displacement> = emptySet(),
        effect: Effect? = null,
    ) = Card(id, name, increments, value, rank, affected, effect)
}
