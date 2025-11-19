package br.com.gabryel.reginaesanguine.server.domain

import kotlinx.serialization.Serializable

/**
 * DTO representing a game request waiting for another player to join.
 */
@Serializable
data class GameRequestDto(
    val id: String,
    val creatorAccountId: String,
    val creatorDeckStateId: String,
    val status: GameRequestStatus,
    val gameId: String? = null,
    val joinerAccountId: String? = null,
    val joinerDeckStateId: String? = null,
)

@Serializable
enum class GameRequestStatus {
    WAITING,
    STARTING,
    STARTED,
}