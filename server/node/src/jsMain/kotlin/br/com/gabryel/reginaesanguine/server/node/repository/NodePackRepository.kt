package br.com.gabryel.reginaesanguine.server.node.repository

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.CardTier
import br.com.gabryel.reginaesanguine.domain.Displacement
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.effect.None
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
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseWinnerLanesByLoserScore
import br.com.gabryel.reginaesanguine.domain.effect.type.ReplaceAllyDefault
import br.com.gabryel.reginaesanguine.domain.effect.type.ReplaceAllyRaise
import br.com.gabryel.reginaesanguine.domain.effect.type.SpawnCardsPerRank
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import br.com.gabryel.reginaesanguine.server.domain.AmountData
import br.com.gabryel.reginaesanguine.server.domain.CardIdsData
import br.com.gabryel.reginaesanguine.server.domain.PowerMultiplierData
import br.com.gabryel.reginaesanguine.server.domain.RaisePowerByCountData
import br.com.gabryel.reginaesanguine.server.domain.RaisePowerOnStatusData
import br.com.gabryel.reginaesanguine.server.domain.toEffectData
import br.com.gabryel.reginaesanguine.server.node.pg.Pool
import br.com.gabryel.reginaesanguine.server.node.pg.PoolClient
import br.com.gabryel.reginaesanguine.server.repository.PackRepository
import kotlinx.coroutines.await

class NodePackRepository(private val pool: Pool) : PackRepository {
    private val json = gameJsonParser()

    override suspend fun savePack(pack: Pack) = withTransaction(pool) { client ->
        val packId = generateUUID()

        client.query(
            "INSERT INTO pack (id, alias, name) VALUES ($1, $2, $3)",
            arrayOf(packId, pack.id, pack.name),
        ).await()

        if (pack.cards.isEmpty()) return@withTransaction

        val cardIdsAndCards = pack.cards.map { card -> generateUUID() to card }

        client.persistAll(
            "pack_card",
            listOf(
                "id",
                "pack_id",
                "pack_internal_card_id",
                "name",
                "tier",
                "rank",
                "power",
                "spawn_only",
                "increments",
            ),
            cardIdsAndCards,
        ) { (id, card) ->
            val increments = json.encodeToString(card.increments.toList())
            listOf(id, packId, card.id, card.name, card.tier.name, card.rank, card.power, card.spawnOnly, increments)
        }

        client.persistAll(
            "pack_card_effect",
            listOf("id", "type", "target", "affected", "description", "trigger_data", "effect_data"),
            cardIdsAndCards,
        ) { (cardId, card) -> mapEffectColumns(card, cardId) }
    }

    private fun mapEffectColumns(card: Card, cardId: String): List<String> {
        val effect = card.effect
        val effectData = json.encodeToString(effect.toEffectData())
        val triggerData = json.encodeToString(effect.trigger)
        val affectedData = if (effect is EffectWithAffected) {
            json.encodeToString(effect.affected.toList())
        } else {
            json.encodeToString(emptyList<Displacement>())
        }

        val target = if (effect is EffectWithAffected) effect.target.name else "SELF"

        return listOf(cardId, effect.discriminator, target, affectedData, effect.description, triggerData, effectData)
    }

    private suspend fun PoolClient.persistAll(
        table: String,
        columns: List<String>,
        data: List<Pair<String, Card>>,
        mapData: (Pair<String, Card>) -> Iterable<Any>
    ) {
        val columnsSize = columns.size

        val queryValues = data.mapIndexed { itemIndex, _ ->
            val baseIndex = (itemIndex * columnsSize) + 1
            columns.mapIndexed { index, _ -> index }
                .joinToString(prefix = "(", postfix = ")") { """$${baseIndex + it}""" }
        }.joinToString()

        val formattedColumns = columns.joinToString()
        val values = data.flatMap(mapData).toTypedArray()

        query(
            """
            INSERT INTO $table ($formattedColumns)
            VALUES $queryValues
            """.trimIndent(),
            values,
        ).await()
    }

    override suspend fun packExists(alias: String): Boolean {
        val result = pool.query("SELECT COUNT(*) as count FROM pack WHERE alias = $1", arrayOf(alias))
            .await()

        if (result.rows.isEmpty()) return false

        val count = when (val countValue = result.rows[0].count) {
            is Number -> countValue.toInt()
            is String -> js("parseInt(countValue)").unsafeCast<Int>()
            else -> 0
        }

        return count > 0
    }

    override suspend fun findPack(alias: String): Pack? {
        val packResult = pool.query("SELECT id, alias, name FROM pack WHERE alias = $1", arrayOf(alias))
            .await()

        if (packResult.rows.isEmpty()) {
            console.log("DEBUG: Pack with $alias not found")
            return null
        }

        val packRow = packResult.rows[0]
        val packUuid = packRow.id

        val cardsResult = pool.query(
            """
            SELECT id, pack_internal_card_id, name, tier, rank, power, spawn_only, increments
            FROM pack_card
            WHERE pack_id = $1
            """.trimIndent(),
            arrayOf(packUuid),
        ).await()

        val cards = cardsResult.rows.map { cardRow ->
            val cardId = cardRow.id
            val effect = loadEffect(cardId) ?: NoEffect

            Card(
                id = cardRow.pack_internal_card_id,
                name = cardRow.name,
                tier = CardTier.valueOf(cardRow.tier),
                rank = (cardRow.rank as Number).toInt(),
                power = (cardRow.power as Number).toInt(),
                spawnOnly = cardRow.spawn_only as Boolean,
                increments = json.decodeFromString<List<Displacement>>(JSON.stringify(cardRow.increments)).toSet(),
                effect = effect,
            )
        }

        return Pack(id = packRow.alias, name = packRow.name, cards = cards)
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
            type = row.type,
            target = row.target,
            affected = JSON.stringify(row.affected),
            description = row.description,
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
                RaiseLaneIfWon(amount = data.amount, description = desc)
            }

            "RaiseWinnerLanesByLoserScore" -> RaiseWinnerLanesByLoserScore(description = desc)
            "RaiseRank" -> {
                val data = json.decodeFromString<AmountData>(effectData)
                RaiseRankDefault(amount = data.amount, description = desc)
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
                SpawnCardsPerRank(cardIds = data.cardIds, trigger = actualTrigger, description = desc)
            }

            "AddCardsToHand" -> {
                val data = json.decodeFromString<CardIdsData>(effectData)
                AddCardsToHandDefault(cardIds = data.cardIds, trigger = actualTrigger, description = desc)
            }

            "DestroyCards" -> DestroyCardsDefault(
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
        val result = pool.query("SELECT COUNT(*) as count FROM pack").await()

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

        return packsResult.rows.mapNotNull { row -> findPack(row.alias) }
    }
}
