package br.com.gabryel.reginaesanguine.viewmodel.game.dto

import kotlinx.serialization.Serializable

/**
 * Response DTO for game initialization.
 */
@Serializable
data class GameIdDto(
    val gameId: String
)
