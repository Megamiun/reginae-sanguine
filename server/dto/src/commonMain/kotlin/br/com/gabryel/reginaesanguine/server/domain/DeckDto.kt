package br.com.gabryel.reginaesanguine.server.domain

import kotlinx.serialization.Serializable

@Serializable
data class DeckDto(
    val id: String,
    val stateId: String,
    val packId: String,
    val cardIds: List<String>,
)
