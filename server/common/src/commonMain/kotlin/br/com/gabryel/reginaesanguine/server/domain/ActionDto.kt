package br.com.gabryel.reginaesanguine.server.domain

import br.com.gabryel.reginaesanguine.domain.Action
import br.com.gabryel.reginaesanguine.domain.Position
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data transfer object for Action with concrete String type for cards.
 */
@Serializable
sealed interface ActionDto {
    @Serializable
    @SerialName("Skip")
    data object Skip : ActionDto

    @Serializable
    @SerialName("Play")
    data class Play(val position: Position, val card: String) : ActionDto

    companion object {
        fun from(action: Action<String>): ActionDto = when (action) {
            is Action.Skip -> Skip
            is Action.Play -> Play(action.position, action.card)
        }
    }

    fun toDomain(): Action<out String> = when (this) {
        is Skip -> Action.Skip
        is Play -> Action.Play(position, card)
    }
}
