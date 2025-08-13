package br.com.gabryel.reginaesanguine.domain.effect.type

import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.effect.GameSummarizer
import br.com.gabryel.reginaesanguine.domain.effect.Trigger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface Spawn : Effect {
    fun getSpawns(game: GameSummarizer): Map<Position, String>
}

@Serializable
@SerialName("SpawnCardsPerRank")
class SpawnCardsPerRank(
    val cardIds: List<String>,
    override val trigger: Trigger,
    override val description: String = "Add cards $cardIds to field on $trigger"
) : Spawn {
    override fun getSpawns(game: GameSummarizer): Map<Position, String> {
        TODO("Not yet implemented")
    }
}
