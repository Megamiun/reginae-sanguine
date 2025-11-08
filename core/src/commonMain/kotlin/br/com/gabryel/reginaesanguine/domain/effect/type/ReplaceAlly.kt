package br.com.gabryel.reginaesanguine.domain.effect.type

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Displacement
import br.com.gabryel.reginaesanguine.domain.effect.None
import br.com.gabryel.reginaesanguine.domain.effect.TargetType
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.SELF
import br.com.gabryel.reginaesanguine.domain.effect.WhenPlayed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Replace effects handle card replacement and power modification based on the replaced card.
 * These effects replace a card at the source position and apply power bonuses to target positions.
 */
interface ReplaceAlly : Effect {
    fun getReplaceEffect(replacedCard: Card): Effect
}

@Serializable
@SerialName("ReplaceAlly")
class ReplaceAllyDefault(
    override val description: String = "Destroy ally and put this card in place",
) : ReplaceAlly {
    @Transient
    override val trigger = None

    override fun getReplaceEffect(replacedCard: Card) = NoEffect

    override val discriminator: String = "ReplaceAlly"
}

@Serializable
@SerialName("ReplaceAllyRaise")
class ReplaceAllyRaise(
    val powerMultiplier: Int,
    val target: TargetType,
    val affected: Set<Displacement> = setOf(),
    override val description: String = "Replace ally and raise $target power by replaced card's power × $powerMultiplier",
) : ReplaceAlly {
    @Transient
    override val trigger = None

    override fun getReplaceEffect(replacedCard: Card) =
        RaisePower(replacedCard.power, target, WhenPlayed(SELF), affected)

    override val discriminator: String = "ReplaceAllyRaise"
}
