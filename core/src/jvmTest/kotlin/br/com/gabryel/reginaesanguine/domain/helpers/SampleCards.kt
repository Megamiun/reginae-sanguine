package br.com.gabryel.reginaesanguine.domain.helpers

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Displacement
import br.com.gabryel.reginaesanguine.domain.effect.type.Effect
import br.com.gabryel.reginaesanguine.domain.effect.type.NoEffect

object SampleCards {
    val SECURITY_OFFICER =
        Card(
            "001",
            "Security Officer",
            1,
            1,
            setOf(
                Displacement(1, 0),
                Displacement(0, 1),
                Displacement(-1, 0),
                Displacement(0, -1),
            ),
        )

    val RIOT_TROOPER =
        Card(
            "002",
            "Riot Trooper",
            3,
            2,
            setOf(
                Displacement(1, 0),
                Displacement(2, 0),
                Displacement(0, 1),
                Displacement(-1, 0),
                Displacement(-2, 0),
            ),
        )

    fun cardOf(
        id: String = "Unnamed",
        name: String = id,
        increments: Set<Displacement> = emptySet(),
        power: Int = 1,
        rank: Int = 1,
        effect: Effect = NoEffect,
    ) = Card(id, name, power, rank, increments, effect)
}
