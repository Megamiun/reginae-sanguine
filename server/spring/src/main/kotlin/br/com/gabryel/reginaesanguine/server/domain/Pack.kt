package br.com.gabryel.reginaesanguine.server.domain

import br.com.gabryel.reginaesanguine.domain.Card
import kotlinx.serialization.Serializable

@Serializable
data class Pack(
    val id: String,
    val name: String,
    val cards: List<Card>
)
