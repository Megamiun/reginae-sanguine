package br.com.gabryel.reginaesanguine.server.entity

import br.com.gabryel.reginaesanguine.domain.Displacement
import br.com.gabryel.reginaesanguine.domain.effect.None
import br.com.gabryel.reginaesanguine.domain.effect.StatusType
import br.com.gabryel.reginaesanguine.domain.effect.TargetType
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.SELF
import br.com.gabryel.reginaesanguine.domain.effect.Trigger
import br.com.gabryel.reginaesanguine.domain.effect.type.AddCardsToHandDefault
import br.com.gabryel.reginaesanguine.domain.effect.type.DestroyCardsDefault
import br.com.gabryel.reginaesanguine.domain.effect.type.Effect
import br.com.gabryel.reginaesanguine.domain.effect.type.EffectWithAffected
import br.com.gabryel.reginaesanguine.domain.effect.type.FlavourText
import br.com.gabryel.reginaesanguine.domain.effect.type.NoEffect
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseLaneIfWon
import br.com.gabryel.reginaesanguine.domain.effect.type.RaisePower
import br.com.gabryel.reginaesanguine.domain.effect.type.RaisePowerByCount
import br.com.gabryel.reginaesanguine.domain.effect.type.RaisePowerOnStatus
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseRankDefault
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseWinnerLanesByLoserScore
import br.com.gabryel.reginaesanguine.domain.effect.type.ReplaceAllyDefault
import br.com.gabryel.reginaesanguine.domain.effect.type.ReplaceAllyRaise
import br.com.gabryel.reginaesanguine.domain.effect.type.SpawnCardsPerRank
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.polymorphic
import org.hibernate.annotations.JdbcType
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType
import org.hibernate.type.SqlTypes.JSON
import java.util.UUID

@Entity
@Table(name = "pack_card_effect")
class PackCardEffectEntity(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "type")
    val type: String,
    @Enumerated(STRING)
    @JdbcType(value = PostgreSQLEnumJdbcType::class)
    val target: TargetType,
    val description: String,
    @JdbcTypeCode(JSON)
    val affected: String,
    // Effect-specific data stored as JSON
    @JdbcTypeCode(JSON)
    val triggerData: String,
    @JdbcTypeCode(JSON)
    val effectData: String,
)

// Data classes for effect-specific data
interface EffectData

@Serializable
@SerialName("AmountData")
private data class AmountData(val amount: Int) : EffectData

@Serializable
@SerialName("RaisePowerByCount")
private data class RaisePowerByCountData(val amount: Int, val status: StatusType, val scope: TargetType) : EffectData

@Serializable
@SerialName("RaisePowerOnStatusData")
private data class RaisePowerOnStatusData(val enhancedAmount: Int, val enfeebledAmount: Int) : EffectData

@Serializable
@SerialName("CardIdsData")
private data class CardIdsData(val cardIds: List<String>) : EffectData

@Serializable
@SerialName("PowerMultiplierData")
private data class PowerMultiplierData(val powerMultiplier: Int) : EffectData

@Serializable
@SerialName("EmptyData")
private object EmptyData : EffectData

private val json = gameJsonParser {
    polymorphic(EffectData::class) {
        subclass(AmountData::class, AmountData.serializer())
        subclass(RaisePowerByCountData::class, RaisePowerByCountData.serializer())
        subclass(RaisePowerOnStatusData::class, RaisePowerOnStatusData.serializer())
        subclass(CardIdsData::class, CardIdsData.serializer())
        subclass(PowerMultiplierData::class, PowerMultiplierData.serializer())
        subclass(EmptyData::class, EmptyData.serializer())
    }
}

fun PackCardEffectEntity.toDomain(): Effect {
    val affectedDisplacements = json.decodeFromString<List<Displacement>>(affected).toSet()
    val actualTrigger = json.decodeFromString<Trigger?>(triggerData.orEmpty()) ?: None

    return when (type) {
        "RaisePower" -> {
            val data = extractData<AmountData>()
            RaisePower(
                amount = data.amount,
                target = target,
                trigger = actualTrigger,
                affected = affectedDisplacements,
                description = description,
            )
        }
        "RaisePowerByCount" -> {
            val data = extractData<RaisePowerByCountData>()
            RaisePowerByCount(
                amount = data.amount,
                status = data.status,
                scope = data.scope,
                target = target,
                affected = affectedDisplacements,
                description = description,
            )
        }
        "RaisePowerOnStatus" -> {
            val data = extractData<RaisePowerOnStatusData>()
            RaisePowerOnStatus(
                enhancedAmount = data.enhancedAmount,
                enfeebledAmount = data.enfeebledAmount,
                target = target,
                affected = affectedDisplacements,
                description = description,
            )
        }
        "RaiseLaneIfWon" -> {
            val data = extractData<AmountData>()
            RaiseLaneIfWon(
                amount = data.amount,
                description = description,
            )
        }
        "RaiseWinnerLanesByLoserScore" -> RaiseWinnerLanesByLoserScore(description = description)
        "RaiseRank" -> {
            val data = extractData<AmountData>()
            RaiseRankDefault(amount = data.amount, description = description)
        }
        "ReplaceAllyRaise" -> {
            val data = extractData<PowerMultiplierData>()
            ReplaceAllyRaise(
                powerMultiplier = data.powerMultiplier,
                target = target,
                affected = affectedDisplacements,
                description = description,
            )
        }
        "SpawnCardsPerRank" -> {
            val data = extractData<CardIdsData>()
            SpawnCardsPerRank(
                cardIds = data.cardIds,
                trigger = actualTrigger,
                description = description,
            )
        }
        "AddCardsToHand" -> {
            val data = extractData<CardIdsData>()
            AddCardsToHandDefault(
                cardIds = data.cardIds,
                trigger = actualTrigger,
                description = description,
            )
        }
        "DestroyCards" ->
            DestroyCardsDefault(
                target = target,
                trigger = actualTrigger,
                affected = affectedDisplacements,
                description = description,
            )

        "ReplaceAlly" -> ReplaceAllyDefault(description = description)
        "NoEffect" -> NoEffect
        "FlavourText" -> FlavourText(description)
        else -> error("Unknown effect type: $type")
    }
}

private inline fun <reified T> PackCardEffectEntity.extractData(): T =
    json.decodeFromString<T>(effectData)
        ?: throw IllegalStateException("Data of type ${T::class.simpleName} missing")

fun Effect.toEntity(cardId: UUID): PackCardEffectEntity {
    val targetType = if (this is EffectWithAffected) target else SELF

    val affectedJson = if (this is EffectWithAffected) {
        json.encodeToString(affected.toList())
    } else {
        json.encodeToString(emptyList<Displacement>())
    }

    return PackCardEffectEntity(
        id = cardId,
        type = discriminator,
        target = targetType,
        description = description,
        affected = affectedJson,
        triggerData = json.encodeToString(trigger),
        effectData = json.encodeToString(getEffectData()),
    )
}

private fun Effect.getEffectData(): EffectData = when (this) {
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
