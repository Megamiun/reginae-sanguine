package br.com.gabryel.reginaesanguine.server.repository

import br.com.gabryel.reginaesanguine.server.domain.AccountDeck
import br.com.gabryel.reginaesanguine.server.domain.PageDto
import br.com.gabryel.reginaesanguine.server.entity.UserDeckEntity
import br.com.gabryel.reginaesanguine.server.entity.UserDeckStateEntity
import br.com.gabryel.reginaesanguine.server.jpa.PackJpaRepository
import br.com.gabryel.reginaesanguine.server.jpa.UserDeckHistoryJpaRepository
import br.com.gabryel.reginaesanguine.server.jpa.UserDeckJpaRepository
import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Repository
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Repository
class SpringAccountDeckRepository(
    private val userDeckJpaRepository: UserDeckJpaRepository,
    private val userDeckHistoryJpaRepository: UserDeckHistoryJpaRepository,
    private val packRepository: PackJpaRepository,
) : AccountDeckRepository {
    @Transactional
    override suspend fun create(accountId: String, packAlias: String, cardIds: List<String>): AccountDeck =
        withContext(IO) {
            val packId = requireNotNull(packRepository.findByAlias(packAlias)?.id) {
                "Pack '$packAlias' does not exist."
            }

            val deckEntity = userDeckJpaRepository.save(
                UserDeckEntity(accountId = UUID.fromString(accountId), packId = packId),
            )

            val deckId = requireNotNull(deckEntity.id) { "Deck with id ${deckEntity.id} does not exist." }
            val deckState = UserDeckStateEntity(deckId = deckId, cardIds = cardIds)

            userDeckHistoryJpaRepository.save(deckState).toDomain(deckEntity, packAlias)
        }

    @Transactional
    override suspend fun update(deckId: String, cardIds: List<String>): AccountDeck = withContext(IO) {
        val deckEntity = userDeckJpaRepository.findById(UUID.fromString(deckId)).getOrNull()
        val deckId = requireNotNull(deckEntity?.id) { "Deck with id $deckId does not exist." }

        val deckState = UserDeckStateEntity(deckId = deckId, cardIds = cardIds)

        val packEntity = packRepository.findById(deckEntity.packId).getOrNull()
        val packAlias = requireNotNull(packEntity?.alias) { "Pack with id ${deckEntity.packId} does not exist." }

        userDeckHistoryJpaRepository.save(deckState).toDomain(deckEntity, packAlias)
    }

    override suspend fun findById(id: String): AccountDeck? = withContext(IO) {
        val deckId = UUID.fromString(id)

        val deck = userDeckJpaRepository.findById(deckId).getOrNull()
            ?: return@withContext null
        val state = userDeckHistoryJpaRepository.findTop1ByDeckIdOrderByCreatedAtDesc(deckId)
            ?: return@withContext null
        val packEntity = packRepository.findById(deck.packId).getOrNull()
            ?: return@withContext null

        state.toDomain(deck, packEntity.alias)
    }

    override suspend fun findByStateId(stateId: String): AccountDeck? = withContext(IO) {
        val state = userDeckHistoryJpaRepository.findById(UUID.fromString(stateId)).getOrNull()
            ?: return@withContext null
        val deck = userDeckJpaRepository.findById(state.deckId).getOrNull()
            ?: return@withContext null
        val packEntity = packRepository.findById(deck.packId).getOrNull()
            ?: return@withContext null

        state.toDomain(deck, packEntity.alias)
    }

    override suspend fun findByAccountId(accountId: String): PageDto<AccountDeck> = withContext(IO) {
        val decks = userDeckJpaRepository.findByAccountId(UUID.fromString(accountId)).map { deck ->
            val deckId = requireNotNull(deck.id) { "Deck with id ${deck.id} does not exist." }
            val state = requireNotNull(userDeckHistoryJpaRepository.findTop1ByDeckIdOrderByCreatedAtDesc(deckId)) {
                "Deck State with id ${deck.id} does not exist."
            }
            val packEntity = packRepository.findById(deck.packId).getOrNull()
            val packAlias = requireNotNull(packEntity?.alias) { "Pack with id ${deck.packId} does not exist." }

            state.toDomain(deck, packAlias)
        }

        PageDto.singlePage(decks)
    }

    override suspend fun countByAccountId(accountId: String): Int = withContext(IO) {
        userDeckJpaRepository.countByAccountId(UUID.fromString(accountId))
    }

    override suspend fun existsByIdAndAccountId(id: String, accountId: String): Boolean = withContext(IO) {
        userDeckJpaRepository.existsByIdAndAccountId(UUID.fromString(id), UUID.fromString(accountId))
    }
}
