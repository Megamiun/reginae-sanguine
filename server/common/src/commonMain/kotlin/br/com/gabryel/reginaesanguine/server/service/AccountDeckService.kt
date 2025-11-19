package br.com.gabryel.reginaesanguine.server.service

import br.com.gabryel.reginaesanguine.server.domain.AccountDeck
import br.com.gabryel.reginaesanguine.server.domain.DeckDto
import br.com.gabryel.reginaesanguine.server.domain.action.CreateDeckRequest
import br.com.gabryel.reginaesanguine.server.domain.action.UpdateDeckRequest
import br.com.gabryel.reginaesanguine.server.domain.page.DeckPageDto
import br.com.gabryel.reginaesanguine.server.domain.page.map
import br.com.gabryel.reginaesanguine.server.repository.AccountDeckRepository

class AccountDeckService(private val accountDeckRepository: AccountDeckRepository) {
    companion object {
        const val MAX_DECKS_PER_ACCOUNT = 6
        const val REQUIRED_DECK_SIZE = 15
    }

    suspend fun create(accountId: String, request: CreateDeckRequest): DeckDto {
        validateDeckSize(request.cardIds)
        validateMaxDecks(accountId)

        return accountDeckRepository.create(accountId, request.packAlias, request.cardIds).toDto()
    }

    suspend fun update(accountId: String, deckId: String, request: UpdateDeckRequest): DeckDto {
        validateDeckSize(request.cardIds)
        validateMaxDecks(accountId)

        return accountDeckRepository.update(deckId, request.cardIds).toDto()
    }

    suspend fun getById(accountId: String, deckId: String): DeckDto {
        validateOwnership(accountId, deckId)

        return requireNotNull(accountDeckRepository.findById(deckId)) { "Deck $deckId not found" }
            .toDto()
    }

    suspend fun getAllByAccountId(accountId: String): DeckPageDto =
        accountDeckRepository.findByAccountId(accountId).map { it.toDto() }.upcast(::DeckPageDto)

    private suspend fun validateMaxDecks(accountId: String) {
        val deckCount = accountDeckRepository.countByAccountId(accountId)
        require(deckCount <= MAX_DECKS_PER_ACCOUNT) { "Maximum number of decks reached ($MAX_DECKS_PER_ACCOUNT)" }
    }

    private fun validateDeckSize(cardIds: List<String>) {
        require(cardIds.size == REQUIRED_DECK_SIZE) { "Deck must have exactly $REQUIRED_DECK_SIZE cards, but has ${cardIds.size}" }
    }

    private suspend fun validateOwnership(accountId: String, deckId: String) {
        require(!accountDeckRepository.existsByIdAndAccountId(deckId, accountId)) {
            "Deck not found or access denied"
        }
    }

    private fun AccountDeck.toDto() = DeckDto(id = id, stateId = stateId, packId = packId, cardIds = cardIds)
}
