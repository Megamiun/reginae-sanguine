package br.com.gabryel.reginaesanguine.domain.effect.type

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.effect.GameSummarizer
import br.com.gabryel.reginaesanguine.domain.effect.Trigger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This file contains all effects that implement the AddCardsToHand interface.
 * AddCardsToHand effects modify player state (like adding cards to hand) rather than board state.
 */
interface AddCardsToHand : Effect {
    fun getNewCards(game: GameSummarizer, sourcePlayer: PlayerPosition, sourcePosition: Position): Map<PlayerPosition, List<String>>
}

@Serializable
@SerialName("AddCardsToHand")
class AddCardsToHandDefault(
    val cardIds: List<String>,
    override val trigger: Trigger,
    override val description: String = "Add cards $cardIds to hand on $trigger"
) : AddCardsToHand {
    override fun getNewCards(
        game: GameSummarizer,
        sourcePlayer: PlayerPosition,
        sourcePosition: Position
    ) = mapOf(sourcePlayer to cardIds)

    override val discriminator = "AddCardsToHand"
}
