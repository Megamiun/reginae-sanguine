package br.com.gabryel.reginaesanguine.domain.effect

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface Effect {
    val trigger: Trigger
    val description: String
}

@Serializable
@SerialName("RaisePower")
class RaisePower(
    val amount: Int = 1,
    val target: TargetType,
    override val trigger: Trigger,
    override val description: String = "Raises $target power by $amount on $trigger",
) : Effect

@Serializable
@SerialName("RaiseRank")
class RaiseRank(
    val amount: Int = 1,
    override val trigger: Trigger,
    override val description: String = "Raises rank by $amount on $trigger",
) : Effect

@Serializable
@SerialName("AddCardsToHand")
class AddCardsToHand(
    val cardIds: List<String>,
    override val trigger: Trigger,
    override val description: String = "Add cards $cardIds to hand on $trigger",
) : Effect

@Serializable
@SerialName("SpawnCards")
class SpawnCards(
    val cardIds: List<String>,
    override val trigger: Trigger,
    override val description: String = "Add cards $cardIds to field on $trigger",
) : Effect

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
    val target: TargetType,
    override val trigger: Trigger,
    override val description: String = "Destroy $target cards on $trigger",
) : Effect

@Serializable
@SerialName("ReplaceAlly")
class ReplaceAlly(
    val powerMultiplier: Int = 0,
    val target: TargetType,
    override val trigger: Trigger,
    override val description: String = "Replace ally and raises $target power per $powerMultiplier on $trigger",
) : Effect

@Serializable
@SerialName("StatusBonus")
class StatusBonus(
    val enhancedAmount: Int,
    val enfeebledAmount: Int,
    override val description: String = "Raises $enhancedAmount power on enhanced and $enfeebledAmount power on enfeebled",
) : Effect {
    override val trigger = OnStatusChange()
}

@Serializable
@SerialName("FlavourText")
class FlavourText(override val description: String) : Effect {
    override val trigger = None
}
