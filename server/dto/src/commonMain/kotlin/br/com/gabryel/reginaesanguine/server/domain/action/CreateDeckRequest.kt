package br.com.gabryel.reginaesanguine.server.domain.action

import kotlinx.serialization.Serializable

@Serializable
data class CreateDeckRequest(
    val packAlias: String,
    val cardIds: List<String>,
)
