package br.com.gabryel.reginaesanguine.domain.effect.type

import br.com.gabryel.reginaesanguine.domain.Displacement
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.effect.GameSummarizer
import br.com.gabryel.reginaesanguine.domain.effect.None
import br.com.gabryel.reginaesanguine.domain.effect.TargetType
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.NONE
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.SELF
import br.com.gabryel.reginaesanguine.domain.effect.Trigger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

sealed interface Effect {
    val trigger: Trigger
    val description: String
}

sealed interface EffectWithAffected : Effect {
    val affected: Set<Displacement>

    val target: TargetType

    fun getAffectedPositions(sourcePosition: Position, sourcePlayer: PlayerPosition): List<Position> =
        when (target) {
            SELF -> listOf(sourcePosition)
            else -> affected.map { displacement -> sourcePosition + sourcePlayer.correct(displacement) }
        }
}

@Serializable
@SerialName("RaiseRank")
class RaiseRank(
    val amount: Int = 1,
    override val description: String = "Raises rank by $amount"
) : Effect {
    @Transient
    override val trigger = None
}

@Serializable
@SerialName("SpawnCards")
class SpawnCards(
    val cardIds: List<String>,
    override val trigger: Trigger,
    override val description: String = "Add cards $cardIds to field on $trigger"
) : Effect

@Serializable
@SerialName("FlavourText")
class FlavourText(override val description: String) : Effect {
    @Transient
    override val trigger = None
}

@Serializable
@SerialName("NoEffect")
object NoEffect : Effect {
    override val description = "NONE"
    override val trigger = None
}
