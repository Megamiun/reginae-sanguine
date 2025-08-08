package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.Displacement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

interface Effect {
    val trigger: Trigger
    val description: String
}

interface EffectWithAffected : Effect {
    val affected: Set<Displacement>
}

interface Targetable {
    val target: TargetType
}

@Serializable
@SerialName("RaisePower")
class RaisePower(
    val amount: Int = 1,
    override val target: TargetType,
    override val trigger: Trigger,
    override val description: String = "Raises $target power by $amount on $trigger",
    override val affected: Set<Displacement> = setOf(),
) : EffectWithAffected, Targetable

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
@SerialName("AddCardsToHand")
class AddCardsToHand(
    val cardIds: List<String>,
    override val trigger: Trigger,
    override val description: String = "Add cards $cardIds to hand on $trigger"
) : Effect

@Serializable
@SerialName("SpawnCards")
class SpawnCards(
    val cardIds: List<String>,
    override val trigger: Trigger,
    override val description: String = "Add cards $cardIds to field on $trigger",
    override val affected: Set<Displacement> = setOf(),
) : EffectWithAffected

@Serializable
@SerialName("ScoreBonus")
class ScoreBonus(
    val amount: Int,
    override val trigger: Trigger,
    override val description: String = "Add $amount score on $trigger",
) : Effect

@Serializable
@SerialName("LoserScoreBonus")
class LoserScoreBonus(
    override val trigger: Trigger,
    override val description: String = "Add points from loser to score on $trigger",
) : Effect

@Serializable
@SerialName("DestroyCards")
class DestroyCards(
    override val target: TargetType,
    override val trigger: Trigger,
    override val description: String = "Destroy $target cards on $trigger",
    override val affected: Set<Displacement> = setOf(),
) : EffectWithAffected, Targetable

@Serializable
@SerialName("ReplaceAlly")
class ReplaceAlly(
    val powerMultiplier: Int = 0,
    override val target: TargetType,
    override val trigger: Trigger,
    override val description: String = "Replace ally and raises $target power per $powerMultiplier on $trigger",
) : Effect, Targetable

@Serializable
@SerialName("StatusBonus")
class StatusBonus(
    val enhancedAmount: Int,
    val enfeebledAmount: Int,
    override val target: TargetType,
    override val description: String = "Raises $enhancedAmount power on enhanced and $enfeebledAmount power on enfeebled",
    override val affected: Set<Displacement> = setOf(),
) : EffectWithAffected, Targetable {
    @Transient
    override val trigger = OnStatusChange()
}

@Serializable
@SerialName("FlavourText")
class FlavourText(override val description: String) : Effect {
    @Transient
    override val trigger = None
}
