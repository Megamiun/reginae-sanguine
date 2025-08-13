package br.com.gabryel.reginaesanguine.domain.effect.type

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.effect.GameSummarizer
import br.com.gabryel.reginaesanguine.domain.effect.Trigger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class PlayerModification(
    val cardsToAdd: List<String> = emptyList()
)

/**
 * This file contains all effects that implement the PlayerEffect interface.
 * PlayerEffect effects modify player state (like adding cards to hand) rather than board state.
 */
interface PlayerEffect : Effect {
    fun getPlayerModifications(
        game: GameSummarizer,
        sourcePlayer: PlayerPosition,
        sourcePosition: Position
    ): Map<PlayerPosition, PlayerModification>
}

@Serializable
@SerialName("AddCardsToHand")
class AddCardsToHand(
    val cardIds: List<String>,
    override val trigger: Trigger,
    override val description: String = "Add cards $cardIds to hand on $trigger"
) : PlayerEffect {
    override fun getPlayerModifications(
        game: GameSummarizer,
        sourcePlayer: PlayerPosition,
        sourcePosition: Position
    ): Map<PlayerPosition, PlayerModification> =
        mapOf(sourcePlayer to PlayerModification(cardsToAdd = cardIds))
}
