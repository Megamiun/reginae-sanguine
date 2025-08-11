package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.Displacement
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.NONE
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.SELF
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

interface Effect {
    val trigger: Trigger
    val description: String
}

interface Raisable {
    val target: TargetType

    fun getRaiseBy(
        game: GameSummarizer,
        source: PlayerPosition,
        targeted: PlayerPosition,
        sourcePosition: Position,
        self: Boolean
    ): Int = if (target.isTargetable(source, targeted, self))
        getDefaultAmount(game, source, sourcePosition, self)
    else 0

    fun getDefaultAmount(game: GameSummarizer, sourcePlayer: PlayerPosition, sourcePosition: Position, self: Boolean): Int
}

interface EffectWithAffected : Effect {
    val affected: Set<Displacement>

    val target: TargetType

    fun getAffectedPositions(sourcePosition: Position, sourcePlayer: PlayerPosition): List<Position> =
        when (target) {
            SELF -> listOf(sourcePosition)
            else -> affected.map { displacement -> sourcePosition + sourcePlayer.correct(displacement) }
        }
}

@Serializable
@SerialName("RaisePower")
class RaisePower(
    val amount: Int = 1,
    override val target: TargetType,
    override val trigger: Trigger,
    override val affected: Set<Displacement> = setOf(),
    override val description: String = "Raises $target power by $amount on $trigger",
) : EffectWithAffected, Raisable {
    override fun getDefaultAmount(game: GameSummarizer, sourcePlayer: PlayerPosition, sourcePosition: Position, self: Boolean): Int =
        amount
}

@Serializable
@SerialName("RaisePowerByCount")
class RaisePowerByCount(
    val status: StatusType = StatusType.ANY,
    val scope: TargetType = TargetType.ANY,
    override val target: TargetType,
    override val description: String = "Raises $target power by count of cards with status $status owned by $scope",
    override val affected: Set<Displacement> = setOf(),
) : EffectWithAffected, Raisable {
    @Transient
    override val trigger = WhileActive

    override fun getDefaultAmount(game: GameSummarizer, sourcePlayer: PlayerPosition, sourcePosition: Position, self: Boolean): Int =
        game.getOccupiedCells().count { (targetPosition, cell) ->
            val owner = cell.owner ?: return@count false

            if (!scope.isTargetable(sourcePlayer, owner, self))
                return@count false

            status.isUnderStatus(game.getExtraPowerAt(targetPosition))
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
    override val description: String = "Add cards $cardIds to field on $trigger"
) : Effect

@Serializable
@SerialName("ScoreBonus")
class ScoreBonus(
    val amount: Int,
    override val description: String = "Add $amount score to lane if you wins lane",
) : Effect {
    @Transient
    override val trigger = WhenLaneWon
}

@Serializable
@SerialName("LoserScoreBonus")
class LoserScoreBonus(
    override val description: String = "Add points from every lane loser score to winner score",
) : Effect {
    @Transient
    override val trigger = WhenLaneWon
}

@Serializable
@SerialName("DestroyCards")
class DestroyCards(
    override val target: TargetType,
    override val trigger: Trigger,
    override val affected: Set<Displacement> = setOf(),
    override val description: String = "Destroy $target cards on $trigger",
) : EffectWithAffected

@Serializable
@SerialName("ReplaceAlly")
class ReplaceAlly(
    val powerMultiplier: Int = 0,
    override val target: TargetType = NONE,
    override val affected: Set<Displacement> = setOf(),
    override val description: String = "Replace ally and raises $target power per $powerMultiplier",
) : EffectWithAffected {
    @Transient
    override val trigger = None
}

@Serializable
@SerialName("StatusBonus")
class StatusBonus(
    val enhancedAmount: Int,
    val enfeebledAmount: Int,
    override val target: TargetType,
    override val description: String = "Raises $enhancedAmount power on enhanced and $enfeebledAmount power on enfeebled",
    override val affected: Set<Displacement> = setOf(),
) : EffectWithAffected, Raisable {
    @Transient
    override val trigger = WhileActive

    override fun getDefaultAmount(game: GameSummarizer, sourcePlayer: PlayerPosition, sourcePosition: Position, self: Boolean): Int {
        val netPowerOnSource = game.getExtraPowerAt(sourcePosition)

        return when {
            netPowerOnSource > 0 -> enhancedAmount
            netPowerOnSource < 0 -> enfeebledAmount
            else -> 0
        }
    }
}

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
