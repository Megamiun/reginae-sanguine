package br.com.gabryel.reginaesanguine.server.repository

import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.server.domain.page.GenericPageDto
import br.com.gabryel.reginaesanguine.server.domain.page.PageDto
import br.com.gabryel.reginaesanguine.server.entity.PackCardEntity
import br.com.gabryel.reginaesanguine.server.entity.PackEntity
import br.com.gabryel.reginaesanguine.server.entity.toDomain
import br.com.gabryel.reginaesanguine.server.entity.toEntity
import br.com.gabryel.reginaesanguine.server.jpa.PackCardEffectJpaRepository
import br.com.gabryel.reginaesanguine.server.jpa.PackCardJpaRepository
import br.com.gabryel.reginaesanguine.server.jpa.PackJpaRepository
import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class SpringPackRepository(
    private val packJpaRepository: PackJpaRepository,
    private val packCardJpaRepository: PackCardJpaRepository,
    private val packCardEffectJpaRepository: PackCardEffectJpaRepository,
) : PackRepository {
    @Transactional
    override suspend fun savePack(pack: Pack): Unit = withContext(IO) {
        val packEntity = PackEntity.fromDomain(pack)
        packJpaRepository.save(packEntity)

        val cardEntities = pack.cards.map { domainCard -> PackCardEntity.fromDomain(domainCard, packEntity) }
        val savedCards = packCardJpaRepository.saveAll(cardEntities).toList()

        val effectEntities = savedCards.zip(pack.cards).map { (cardEntity, domainCard) ->
            val cardId = requireNotNull(cardEntity.id)
            domainCard.effect.toEntity(cardId)
        }
        packCardEffectJpaRepository.saveAll(effectEntities)
    }

    override suspend fun packExists(alias: String): Boolean = withContext(IO) {
        packJpaRepository.existsByAlias(alias)
    }

    override suspend fun findPack(alias: String): Pack? = withContext(IO) {
        requireNotNull(packJpaRepository.findByAlias(alias)) { "Pack '$alias' does not exist." }
            .toPackDomain()
    }

    override suspend fun countPacks(): Long = withContext(IO) {
        packJpaRepository.count()
    }

    override suspend fun findAllPacks(page: Int, size: Int): PageDto<Pack> = withContext(IO) {
        val dbPage = packJpaRepository.findAll(Pageable.ofSize(size).withPage(page))

        GenericPageDto(
            dbPage.content.map { it.toPackDomain() },
            page,
            size,
            dbPage.totalElements,
            dbPage.totalPages,
        )
    }

    private fun PackEntity.toPackDomain(): Pack {
        val packId = requireNotNull(id) { "Pack ID not found" }
        val cardEntities = packCardJpaRepository.findByPackId(packId)

        val cardIds = cardEntities.map { requireNotNull(it.id) }
        val effectsMap = packCardEffectJpaRepository.findAllById(cardIds).associateBy { it.id }

        val cards = cardEntities.map { cardEntity ->
            val cardId = requireNotNull(cardEntity.id) { "Card ID not found" }
            val effectEntity = effectsMap[cardId]
            val effect = effectEntity?.toDomain()
            cardEntity.toDomain(effect)
        }

        return toDomain(cards)
    }
}
