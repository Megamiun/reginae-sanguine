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
interface Replace : Effect {
    fun getReplaceEffect(replacedCard: Card): Effect
}

@Serializable
@SerialName("ReplaceAlly")
class ReplaceAlly(
    val powerMultiplier: Int = 0,
    val target: TargetType = SELF,
    val affected: Set<Displacement> = setOf(),
    override val description: String = "Replace ally and raise $target power by replaced card's power × $powerMultiplier",
) : Replace {
    @Transient
    override val trigger = None

    override fun getReplaceEffect(replacedCard: Card) =
        RaisePower(replacedCard.power, target, WhenPlayed(SELF), affected)
}
