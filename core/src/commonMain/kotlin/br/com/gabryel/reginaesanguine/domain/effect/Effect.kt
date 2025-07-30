package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ANY

interface Effect {
    val trigger: Trigger
    val description: String
}

class RaisePower(
    val amount: Int = 1,
    val target: TargetType = ANY,
    override val description: String,
    override val trigger: Trigger,
) : Effect

class RaiseRank(
    val amount: Int = 1,
    override val description: String,
    override val trigger: Trigger,
) : Effect

class AddCardsToHand(
    val cardIds: List<String>,
    override val description: String,
    override val trigger: Trigger,
) : Effect

class SpawnCards(
    val cardIds: List<String> = listOf(),
    override val description: String,
    override val trigger: Trigger,
) : Effect

class ScoreBonus(
    val points: Int = 1,
    override val description: String,
    override val trigger: Trigger,
) : Effect

class FromLoserScoreBonus(
    override val description: String,
    override val trigger: Trigger,
) : Effect

class DestroyCards(
    val target: TargetType = ANY,
    override val description: String,
    override val trigger: Trigger,
) : Effect

class ReplaceAlly(
    val powerMultiplier: Int = 0,
    val target: TargetType = ANY,
    override val description: String,
    override val trigger: Trigger,
) : Effect

class StatusBonus(
    val enhancedAmount: Int,
    val enfeebledAmount: Int,
    override val description: String,
    override val trigger: Trigger,
) : Effect

class FlavourText(override val description: String) : Effect {
    override val trigger = None
}
