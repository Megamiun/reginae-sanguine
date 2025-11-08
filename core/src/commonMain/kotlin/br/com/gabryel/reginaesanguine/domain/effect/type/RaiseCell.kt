package br.com.gabryel.reginaesanguine.domain.effect.type

import br.com.gabryel.reginaesanguine.domain.Displacement
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.effect.GameSummarizer
import br.com.gabryel.reginaesanguine.domain.effect.StatusType
import br.com.gabryel.reginaesanguine.domain.effect.TargetType
import br.com.gabryel.reginaesanguine.domain.effect.Trigger
import br.com.gabryel.reginaesanguine.domain.effect.WhileActive
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * This file contains all effects that implement the RaiseCell interface.
 * RaiseCell effects modify card power values on the board.
 */
interface RaiseCell : EffectWithAffected {
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

@Serializable
@SerialName("RaisePower")
class RaisePower(
    val amount: Int,
    override val target: TargetType,
    override val trigger: Trigger,
    override val affected: Set<Displacement> = setOf(),
    override val description: String = "Raises $target power by $amount on $trigger",
) : RaiseCell {
    override fun getDefaultAmount(game: GameSummarizer, sourcePlayer: PlayerPosition, sourcePosition: Position, self: Boolean): Int =
        amount

    override val discriminator = "RaisePower"
}

@Serializable
@SerialName("RaisePowerByCount")
class RaisePowerByCount(
    val amount: Int,
    val status: StatusType,
    val scope: TargetType,
    override val target: TargetType,
    override val affected: Set<Displacement> = setOf(),
    override val description: String = "Raises $target power by count of cards with status $status owned by $scope",
) : RaiseCell {
    @Transient
    override val trigger = WhileActive

    override fun getDefaultAmount(game: GameSummarizer, sourcePlayer: PlayerPosition, sourcePosition: Position, self: Boolean): Int =
        game.getOccupiedCells().count { (targetPosition, cell) ->
            val owner = cell.owner ?: return@count false

            if (!scope.isTargetable(sourcePlayer, owner, self))
                return@count false

            status.isUnderStatus(game.getExtraPowerAt(targetPosition))
        } * amount

    override val discriminator = "RaisePowerByCount"
}

@Serializable
@SerialName("RaisePowerOnStatus")
class RaisePowerOnStatus(
    val enhancedAmount: Int,
    val enfeebledAmount: Int,
    override val target: TargetType,
    override val affected: Set<Displacement> = setOf(),
    override val description: String = "Raises $enhancedAmount power on enhanced and $enfeebledAmount power on enfeebled",
) : RaiseCell {
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

    override val discriminator = "RaisePowerOnStatus"
}
