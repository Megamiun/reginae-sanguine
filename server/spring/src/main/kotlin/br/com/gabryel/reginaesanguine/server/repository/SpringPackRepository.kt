package br.com.gabryel.reginaesanguine.server.repository

import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.server.entity.PackCardEntity
import br.com.gabryel.reginaesanguine.server.entity.PackEntity
import br.com.gabryel.reginaesanguine.server.entity.toDomain
import br.com.gabryel.reginaesanguine.server.entity.toEntity
import br.com.gabryel.reginaesanguine.server.jpa.PackCardEffectJpaRepository
import br.com.gabryel.reginaesanguine.server.jpa.PackCardJpaRepository
import br.com.gabryel.reginaesanguine.server.jpa.PackJpaRepository
import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Repository

@Repository
class SpringPackRepository(
    private val packJpaRepository: PackJpaRepository,
    private val packCardJpaRepository: PackCardJpaRepository,
    private val packCardEffectJpaRepository: PackCardEffectJpaRepository,
) : PackRepository {
    @Transactional
    override suspend fun savePack(pack: Pack) = withContext(Dispatchers.IO) {
        val packEntity = PackEntity.fromDomain(pack)
        packJpaRepository.save(packEntity)

        // TODO Use a single saveAll for both
        pack.cards.forEach { domainCard ->
            val cardEntity = PackCardEntity.fromDomain(domainCard, packEntity)
            packCardJpaRepository.save(cardEntity)

            val cardId = requireNotNull(cardEntity.id)
            packCardEffectJpaRepository.save(domainCard.effect.toEntity(cardId))
        }
    }

    override suspend fun packExists(alias: String): Boolean = withContext(Dispatchers.IO) {
        packJpaRepository.existsByAlias(alias)
    }

    override suspend fun findPack(alias: String): Pack? = withContext(Dispatchers.IO) {
        val pack = packJpaRepository.findByAlias(alias)
        toPackDomain(pack)
    }

    override suspend fun countPacks(): Long = withContext(Dispatchers.IO) {
        packJpaRepository.count()
    }

    override suspend fun findAllPacks(page: Int, size: Int): List<Pack> = withContext(Dispatchers.IO) {
        val offset = page * size
        // TODO Change to Pageable query
        packJpaRepository.findAll().drop(offset).take(size).map { toPackDomain(it) }
    }

    private fun toPackDomain(pack: PackEntity): Pack {
        val packId = requireNotNull(pack.id) { "Pack ID not found" }
        // TODO Avoid a N+1 here
        val cards = packCardJpaRepository.findByPackId(packId).map { cardEntity ->
            val cardId = requireNotNull(cardEntity.id) { "Card ID not found" }

            val effectEntity = packCardEffectJpaRepository.findById(cardId).orElse(null)
            val effect = effectEntity?.toDomain()

            cardEntity.toDomain(effect)
        }

        return pack.toDomain(cards)
    }
}
