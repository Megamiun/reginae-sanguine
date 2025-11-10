package br.com.gabryel.reginaesanguine.server.node.repository

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.CardTier
import br.com.gabryel.reginaesanguine.domain.Displacement
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.effect.None
import br.com.gabryel.reginaesanguine.domain.effect.StatusType
import br.com.gabryel.reginaesanguine.domain.effect.TargetType
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
import br.com.gabryel.reginaesanguine.domain.effect.type.ReplaceAllyDefault
import br.com.gabryel.reginaesanguine.domain.effect.type.ReplaceAllyRaise
import br.com.gabryel.reginaesanguine.domain.effect.type.SpawnCardsPerRank
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import br.com.gabryel.reginaesanguine.server.node.pg.Pool
import br.com.gabryel.reginaesanguine.server.node.pg.PoolClient
import br.com.gabryel.reginaesanguine.server.repository.PackRepository
import kotlinx.coroutines.await
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class NodePackRepository(private val pool: Pool) : PackRepository {
    private val json = gameJsonParser()

    override suspend fun savePack(pack: Pack) {
        withTransaction(pool) { client ->
            val packId = generateUUID()

            client.query(
                "INSERT INTO pack (id, alias, name) VALUES ($1, $2, $3)",
                arrayOf(packId, pack.id, pack.name),
            ).await()

            // TODO Do this in just one insert. Same for Effects
            pack.cards.forEach { card ->
                val cardId = generateUUID()

                client.query(
                    """
                    INSERT INTO pack_card (id, pack_id, pack_internal_card_id, name, tier, rank, power, spawn_only, increments)
                    VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
                    """.trimIndent(),
                    arrayOf(
                        cardId,
                        packId,
                        card.id,
                        card.name,
                        card.tier.name,
                        card.rank,
                        card.power,
                        card.spawnOnly,
                        json.encodeToString(card.increments.toList()),
                    ),
                ).await()

                saveEffect(client, cardId, card.effect)
            }
        }
    }

    private suspend fun saveEffect(client: PoolClient, cardId: String, effect: Effect) {
        val effectData = json.encodeToString(effect.getEffectData())
        val triggerData = json.encodeToString(effect.trigger)
        val affectedData = if (effect is EffectWithAffected) {
            json.encodeToString(effect.affected.toList())
        } else {
            json.encodeToString(emptyList<Displacement>())
        }

        val target = if (effect is EffectWithAffected) {
            effect.target.name
        } else {
            "SELF"
        }

        client.query(
            """
            INSERT INTO pack_card_effect (id, type, target, affected, description, trigger_data, effect_data)
            VALUES ($1, $2, $3, $4, $5, $6, $7)
            """.trimIndent(),
            arrayOf(cardId, effect.discriminator, target, affectedData, effect.description, triggerData, effectData),
        ).await()
    }

    override suspend fun packExists(alias: String): Boolean {
        console.log("DEBUG: packExists - checking for alias: $alias")
        val result = pool.query(
            "SELECT COUNT(*) as count FROM pack WHERE alias = $1",
            arrayOf(alias),
        ).await()

        console.log("DEBUG: packExists - result.rows:", result.rows)

        if (result.rows.isEmpty()) {
            console.log("DEBUG: packExists - no rows returned, returning false")
            return false
        }

        val count = when (val countValue = result.rows[0].count) {
            is Number -> countValue.toInt()
            is String -> js("parseInt(countValue)").unsafeCast<Int>()
            else -> 0
        }

        val exists = count > 0
        console.log("DEBUG: packExists - count: $count, returning: $exists")
        return exists
    }

    override suspend fun findPack(alias: String): Pack? {
        val packResult = pool.query(
            "SELECT id, alias, name FROM pack WHERE alias = $1",
            arrayOf(alias),
        ).await()

        if (packResult.rows.isEmpty()) {
            console.log("DEBUG: Pack not found")
            return null
        }

        console.log("DEBUG: Pack not found")

        val packRow = packResult.rows[0]
        val packUuid = packRow.id as String

        val cardsResult = pool.query(
            """
            SELECT id, pack_internal_card_id, name, tier, rank, power, spawn_only, increments
            FROM pack_card
            WHERE pack_id = $1
            """.trimIndent(),
            arrayOf(packUuid),
        ).await()

        val cards = cardsResult.rows.map { cardRow ->
            val cardId = cardRow.id as String

            val effect = loadEffect(cardId) ?: NoEffect

            Card(
                id = cardRow.pack_internal_card_id as String,
                name = cardRow.name as String,
                tier = CardTier.valueOf(cardRow.tier as String),
                rank = (cardRow.rank as Number).toInt(),
                power = (cardRow.power as Number).toInt(),
                spawnOnly = cardRow.spawn_only as Boolean,
                increments = json.decodeFromString<List<Displacement>>(JSON.stringify(cardRow.increments)).toSet(),
                effect = effect,
            )
        }

        return Pack(id = packRow.alias as String, name = packRow.name as String, cards = cards)
    }

    private suspend fun loadEffect(cardId: String): Effect? {
        val effectResult = pool.query(
            """
            SELECT type, target, affected, description, trigger_data, effect_data
            FROM pack_card_effect
            WHERE id = $1
            """.trimIndent(),
            arrayOf(cardId),
        ).await()

        if (effectResult.rows.isEmpty()) return null

        val row = effectResult.rows[0]

        return parseEffect(
            type = row.type as String,
            target = row.target as String,
            affected = JSON.stringify(row.affected),
            description = row.description as? String,
            triggerData = JSON.stringify(row.trigger_data),
            effectData = JSON.stringify(row.effect_data),
        )
    }

    private fun parseEffect(
        type: String,
        target: String,
        affected: String,
        description: String?,
        triggerData: String,
        effectData: String
    ): Effect {
        val affectedDisplacements = json.decodeFromString<List<Displacement>>(affected).toSet()
        val actualTrigger = json.decodeFromString<Trigger?>(triggerData) ?: None
        val targetType = TargetType.valueOf(target)
        val desc = description ?: ""

        return when (type) {
            "RaisePower" -> {
                val data = json.decodeFromString<AmountData>(effectData)
                RaisePower(
                    amount = data.amount,
                    target = targetType,
                    trigger = actualTrigger,
                    affected = affectedDisplacements,
                    description = desc,
                )
            }
            "RaisePowerByCount" -> {
                val data = json.decodeFromString<RaisePowerByCountData>(effectData)
                RaisePowerByCount(
                    amount = data.amount,
                    status = data.status,
                    scope = data.scope,
                    target = targetType,
                    affected = affectedDisplacements,
                    description = desc,
                )
            }
            "RaisePowerOnStatus" -> {
                val data = json.decodeFromString<RaisePowerOnStatusData>(effectData)
                RaisePowerOnStatus(
                    enhancedAmount = data.enhancedAmount,
                    enfeebledAmount = data.enfeebledAmount,
                    target = targetType,
                    affected = affectedDisplacements,
                    description = desc,
                )
            }
            "RaiseLaneIfWon" -> {
                val data = json.decodeFromString<AmountData>(effectData)
                RaiseLaneIfWon(
                    amount = data.amount,
                    description = desc,
                )
            }
            "RaiseWinnerLanesByLoserScore" ->
                br.com.gabryel.reginaesanguine.domain.effect.type.RaiseWinnerLanesByLoserScore(description = desc)
            "RaiseRank" -> {
                val data = json.decodeFromString<AmountData>(effectData)
                RaiseRankDefault(
                    amount = data.amount,
                    description = desc,
                )
            }
            "ReplaceAllyRaise" -> {
                val data = json.decodeFromString<PowerMultiplierData>(effectData)
                ReplaceAllyRaise(
                    powerMultiplier = data.powerMultiplier,
                    target = targetType,
                    affected = affectedDisplacements,
                    description = desc,
                )
            }
            "SpawnCardsPerRank" -> {
                val data = json.decodeFromString<CardIdsData>(effectData)
                SpawnCardsPerRank(
                    cardIds = data.cardIds,
                    trigger = actualTrigger,
                    description = desc,
                )
            }
            "AddCardsToHand" -> {
                val data = json.decodeFromString<CardIdsData>(effectData)
                AddCardsToHandDefault(
                    cardIds = data.cardIds,
                    trigger = actualTrigger,
                    description = desc,
                )
            }
            "DestroyCards" ->
                DestroyCardsDefault(
                    target = targetType,
                    trigger = actualTrigger,
                    affected = affectedDisplacements,
                    description = desc,
                )
            "ReplaceAlly" -> ReplaceAllyDefault(description = desc)
            "NoEffect" -> NoEffect
            "FlavourText" -> FlavourText(desc)
            else -> error("Unknown effect type: $type")
        }
    }

    override suspend fun countPacks(): Long {
        console.log("DEBUG: countPacks - querying")
        val result = pool.query("SELECT COUNT(*) as count FROM pack").await()

        console.log("DEBUG: countPacks - result.rows:", result.rows)
        console.log("DEBUG: countPacks - rowCount:", result.rowCount)

        if (result.rows.isEmpty()) return 0

        return when (val countValue = result.rows[0].count) {
            is Number -> countValue.toLong()
            is String -> js("parseInt(countValue)").unsafeCast<Int>().toLong()
            else -> 0
        }
    }

    override suspend fun findAllPacks(page: Int, size: Int): List<Pack> {
        val offset = page * size
        val packsResult = pool.query("SELECT alias FROM pack LIMIT $1 OFFSET $2", arrayOf(size, offset))
            .await()

        console.log(packsResult.rows)

        return packsResult.rows.mapNotNull { row -> findPack(row.alias) }
    }
}

// TODO Exchange to another better impl
private fun generateUUID(): String = js("require('crypto').randomUUID()") as String

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

// Data classes matching Spring implementation
@Serializable
sealed interface EffectData

@Serializable
@SerialName("AmountData")
private data class AmountData(val amount: Int) : EffectData

@Serializable
@SerialName("RaisePowerByCount")
private data class RaisePowerByCountData(
    val amount: Int,
    val status: StatusType,
    val scope: TargetType
) : EffectData

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
private data object EmptyData : EffectData

/**
 * Execute a block of code within a PostgreSQL transaction.
 * Automatically commits on success and rolls back on failure.
 */
private suspend fun <T> withTransaction(pool: Pool, block: suspend (PoolClient) -> T): T {
    val client = pool.connect().await()
    return try {
        client.query("BEGIN").await()
        val result = block(client)
        client.query("COMMIT").await()
        result
    } catch (e: Throwable) {
        try {
            client.query("ROLLBACK").await()
        } catch (rollbackError: Throwable) {
            console.error("Error during rollback: ${rollbackError.message}")
        }
        throw e
    } finally {
        client.release()
    }
}
