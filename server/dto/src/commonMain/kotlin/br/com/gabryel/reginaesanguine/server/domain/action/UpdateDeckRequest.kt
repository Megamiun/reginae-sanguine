package br.com.gabryel.reginaesanguine.server.domain.action

import kotlinx.serialization.Serializable

@Serializable
data class UpdateDeckRequest(
    val cardIds: List<String>,
)
