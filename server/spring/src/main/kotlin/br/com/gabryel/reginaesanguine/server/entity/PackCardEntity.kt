package br.com.gabryel.reginaesanguine.server.entity

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.CardTier
import br.com.gabryel.reginaesanguine.domain.Displacement
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.effect.type.Effect
import br.com.gabryel.reginaesanguine.domain.effect.type.NoEffect
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcType
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@Table(name = "pack")
class PackEntity(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    val alias: String,
    val name: String,
) {
    fun toDomain(cards: List<Card>) = Pack(id = alias, name = name, cards = cards)

    companion object {
        fun fromDomain(pack: Pack) = PackEntity(alias = pack.id, name = pack.name)
    }
}

@Entity
@Table(name = "pack_card")
class PackCardEntity(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "pack_id")
    val pack: PackEntity,
    val packInternalCardId: String,
    val name: String,
    @Enumerated(STRING)
    @JdbcType(value = PostgreSQLEnumJdbcType::class)
    val tier: CardTier,
    val rank: Short,
    val power: Short,
    val spawnOnly: Boolean,
    @JdbcTypeCode(SqlTypes.JSON)
    val increments: String
) {
    fun toDomain(effect: Effect?): Card {
        val increments = json.decodeFromString<List<Displacement>>(increments)

        return Card(
            id = packInternalCardId,
            name = name,
            tier = tier,
            power = power.toInt(),
            rank = rank.toInt(),
            spawnOnly = spawnOnly,
            increments = increments.toSet(),
            effect = effect ?: NoEffect,
        )
    }

    companion object {
        private val json = gameJsonParser()

        fun fromDomain(card: Card, pack: PackEntity) = PackCardEntity(
            null,
            pack = pack,
            packInternalCardId = card.id,
            name = card.name,
            tier = card.tier,
            rank = card.rank.toShort(),
            power = card.power.toShort(),
            spawnOnly = card.spawnOnly,
            increments = json.encodeToString(card.increments.toList()),
        )
    }
}
