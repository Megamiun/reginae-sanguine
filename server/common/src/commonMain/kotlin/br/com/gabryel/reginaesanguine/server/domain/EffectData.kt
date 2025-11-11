package br.com.gabryel.reginaesanguine.server.domain

import br.com.gabryel.reginaesanguine.domain.effect.StatusType
import br.com.gabryel.reginaesanguine.domain.effect.TargetType
import br.com.gabryel.reginaesanguine.domain.effect.type.AddCardsToHandDefault
import br.com.gabryel.reginaesanguine.domain.effect.type.Effect
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseLaneIfWon
import br.com.gabryel.reginaesanguine.domain.effect.type.RaisePower
import br.com.gabryel.reginaesanguine.domain.effect.type.RaisePowerByCount
import br.com.gabryel.reginaesanguine.domain.effect.type.RaisePowerOnStatus
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseRankDefault
import br.com.gabryel.reginaesanguine.domain.effect.type.ReplaceAllyRaise
import br.com.gabryel.reginaesanguine.domain.effect.type.SpawnCardsPerRank
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Sealed interface for effect-specific data used in persistence layer.
 * Shared between Spring (JVM) and Node (JS) implementations.
 */
@Serializable
sealed interface EffectData

@Serializable
@SerialName("AmountData")
data class AmountData(val amount: Int) : EffectData

@Serializable
@SerialName("RaisePowerByCount")
data class RaisePowerByCountData(val amount: Int, val status: StatusType, val scope: TargetType) : EffectData

@Serializable
@SerialName("RaisePowerOnStatusData")
data class RaisePowerOnStatusData(val enhancedAmount: Int, val enfeebledAmount: Int) : EffectData

@Serializable
@SerialName("CardIdsData")
data class CardIdsData(val cardIds: List<String>) : EffectData

@Serializable
@SerialName("PowerMultiplierData")
data class PowerMultiplierData(val powerMultiplier: Int) : EffectData

@Serializable
@SerialName("EmptyData")
data object EmptyData : EffectData

/**
 * Converts a domain Effect to its persistence EffectData representation.
 */
fun Effect.toEffectData(): EffectData = when (this) {
    is AddCardsToHandDefault -> CardIdsData(cardIds = cardIds)
    is SpawnCardsPerRank -> CardIdsData(cardIds = cardIds)
    is RaisePower -> AmountData(amount = amount)
    is RaiseRankDefault -> AmountData(amount = amount)
    is RaiseLaneIfWon -> AmountData(amount = amount)
    is RaisePowerOnStatus -> RaisePowerOnStatusData(enhancedAmount = enhancedAmount, enfeebledAmount = enfeebledAmount)
    is RaisePowerByCount -> RaisePowerByCountData(amount = amount, status = status, scope = scope)
    is ReplaceAllyRaise -> PowerMultiplierData(powerMultiplier = powerMultiplier)
    else -> EmptyData
}
