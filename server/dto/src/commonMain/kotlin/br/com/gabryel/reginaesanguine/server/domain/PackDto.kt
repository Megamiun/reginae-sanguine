package br.com.gabryel.reginaesanguine.server.domain

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Pack
import kotlinx.serialization.Serializable

@Serializable
data class PackDto(
    val id: String,
    val name: String,
    val cards: List<Card>
) {
    companion object {
        fun from(pack: Pack) = PackDto(
            id = pack.id,
            name = pack.name,
            cards = pack.cards,
        )
    }
}
