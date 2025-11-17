package br.com.gabryel.reginaesanguine.server.domain

data class AccountDeck(
    val id: String,
    val stateId: String,
    val accountId: String,
    val packId: String,
    val cardIds: List<String>,
)
