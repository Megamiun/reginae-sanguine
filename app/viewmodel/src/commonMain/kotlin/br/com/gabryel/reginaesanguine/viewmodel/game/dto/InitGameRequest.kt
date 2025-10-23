package br.com.gabryel.reginaesanguine.viewmodel.game.dto

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import kotlinx.serialization.Serializable

/**
 * Request DTO for initializing a new game.
 */
@Serializable
data class InitGameRequest(
    val deckCards: List<String>,
    val position: PlayerPosition,
    val packId: String
)
