package br.com.gabryel.reginaesanguine.domain

import kotlinx.serialization.Serializable

@Serializable
data class Pack(
    val id: String,
    val name: String,
    val cards: List<Card>
)
