package br.com.gabryel.reginaesanguine.domain.effect.type

import br.com.gabryel.reginaesanguine.domain.Displacement
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.effect.None
import br.com.gabryel.reginaesanguine.domain.effect.TargetType
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.SELF
import br.com.gabryel.reginaesanguine.domain.effect.Trigger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

sealed interface Effect {
    val trigger: Trigger
    val description: String
    val discriminator: String
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
@SerialName("FlavourText")
class FlavourText(override val description: String) : Effect {
    @Transient
    override val trigger = None

    override val discriminator = "FlavourText"
}

@Serializable
@SerialName("NoEffect")
object NoEffect : Effect {
    override val description = "This card has no abilities"

    @Transient
    override val trigger = None

    override val discriminator = "NoEffect"
}
