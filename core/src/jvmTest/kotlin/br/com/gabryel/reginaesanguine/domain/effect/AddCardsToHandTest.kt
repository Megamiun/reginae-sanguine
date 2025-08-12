package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.atColumn
import br.com.gabryel.reginaesanguine.domain.helpers.LEFT_COLUMN
import br.com.gabryel.reginaesanguine.domain.helpers.MIDDLE_LANE
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.cardOf
import br.com.gabryel.reginaesanguine.domain.matchers.haveCardsAtHand
import br.com.gabryel.reginaesanguine.domain.matchers.havePlayerOn
import br.com.gabryel.reginaesanguine.domain.matchers.shouldBeSuccess
import br.com.gabryel.reginaesanguine.domain.matchers.shouldBeSuccessfulAnd
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test

class AddCardsToHandTest {
    private val cardToAdd = cardOf("card_to_add", "Card To Add")
    private val triggerCard = cardOf("trigger_card", "Trigger Card", effect = AddCardsToHand(listOf("card_to_add"), WhenPlayed()))

    @Test
    fun `when AddCardsToHand effect is played, should add specified cards to player hand`() {
        val playerWithDeck = Player(
            hand = listOf(triggerCard),
            deck = listOf(cardToAdd),
        )
        val game = Game.forPlayers(playerWithDeck, Player(), drawn = 0)

        val result = game.play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, triggerCard.id))

        result.shouldBeSuccess().havePlayerOn(LEFT) should haveCardsAtHand(cardToAdd)
    }

    @Test
    fun `when AddCardsToHand effect is played, should remove cards from deck`() {
        val playerWithDeck = Player(
            hand = listOf(triggerCard),
            deck = listOf(cardToAdd),
        )
        val game = Game.forPlayers(playerWithDeck, Player(), drawn = 0)

        val result = game.play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, triggerCard.id))

        result.shouldBeSuccess().havePlayerOn(LEFT).deck.size shouldBe 0
    }

    @Test
    fun `when AddCardsToHand effect specifies multiple cards, should add all available cards`() {
        val card1 = cardOf("card1", "Card 1")
        val card2 = cardOf("card2", "Card 2")
        val multiCardEffect = cardOf("multi", "Multi", effect = AddCardsToHand(listOf("card1", "card2"), WhenPlayed()))

        val playerWithDeck = Player(
            hand = listOf(multiCardEffect),
            deck = listOf(card1, card2),
        )
        val game = Game.forPlayers(playerWithDeck, Player(), drawn = 0)

        val result = game.play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, multiCardEffect.id))

        result.shouldBeSuccess().havePlayerOn(LEFT) should haveCardsAtHand(card1, card2)
    }

    @Test
    fun `when AddCardsToHand effect specifies non-existent cards, should only add existing cards`() {
        val existingCard = cardOf("exists", "Existing Card")
        val effectWithMissingCard = cardOf("partial", "Partial", effect = AddCardsToHand(listOf("exists", "missing"), WhenPlayed()))

        val playerWithDeck = Player(
            hand = listOf(effectWithMissingCard),
            deck = listOf(existingCard),
        )
        val game = Game.forPlayers(playerWithDeck, Player(), drawn = 0)

        val result = game.play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, effectWithMissingCard.id))

        result.shouldBeSuccess().havePlayerOn(LEFT) should haveCardsAtHand(existingCard)
    }

    @Test
    fun `AddCardsToHand should return correct player modifications`() {
        val effect = AddCardsToHand(listOf("card1", "card2"), WhenPlayed())
        val mockSummarizer = mockk<GameSummarizer>()

        val modifications = effect.getPlayerModifications(mockSummarizer, LEFT, MIDDLE_LANE atColumn LEFT_COLUMN)

        modifications shouldBe mapOf(LEFT to PlayerModification(cardsToAdd = listOf("card1", "card2")))
    }

    @Test
    fun `AddCardsToHand should have correct description`() {
        val cardIds = listOf("card1", "card2")
        val trigger = WhenPlayed()
        val effect = AddCardsToHand(cardIds, trigger)

        effect.description shouldBe "Add cards $cardIds to hand on $trigger"
    }
}
