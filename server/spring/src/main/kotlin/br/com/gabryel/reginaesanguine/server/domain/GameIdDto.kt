package br.com.gabryel.reginaesanguine.server.domain

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Response DTO for game initialization.
 */
@Serializable
data class GameIdDto(
    val gameId: @Contextual UUID
)
