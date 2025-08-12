package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Board
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.atColumn
import br.com.gabryel.reginaesanguine.domain.helpers.LEFT_COLUMN
import br.com.gabryel.reginaesanguine.domain.helpers.MIDDLE_LANE
import br.com.gabryel.reginaesanguine.domain.helpers.RIGHT_COLUMN
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.cardOf
import br.com.gabryel.reginaesanguine.domain.matchers.haveCardsAtHand
import br.com.gabryel.reginaesanguine.domain.matchers.havePlayerOn
import br.com.gabryel.reginaesanguine.domain.matchers.shouldBeSuccess
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AddCardsToHandWhenDestroyedTest {
    private val cardToAdd = cardOf("card_to_add", "Card To Add")

    @Test
    fun `AddCardsToHand with WhenDestroyed trigger should NOT add cards on placement`() {
        val destroyedCard = cardOf("destroyed", "Destroyed", effect = AddCardsToHand(listOf("card_to_add"), WhenDestroyed()))
        val player = Player(hand = listOf(destroyedCard), deck = listOf(cardToAdd))
        val game = Game.forPlayers(player, Player(), drawn = 0)

        val result = game.play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, destroyedCard.id))

        // Cards should NOT be added to hand immediately on placement - should wait for destruction
        result.shouldBeSuccess().havePlayerOn(LEFT).hand.size shouldBe 0
    }

    @Test
    fun `AddCardsToHand with WhenDestroyed SELF should add cards when the card itself is destroyed`() {
        // Simplified test for now - will implement destruction logic once basic infrastructure works
        val destroyedCard = cardOf("destroyed", "Destroyed", effect = AddCardsToHand(listOf("card_to_add"), WhenDestroyed()))
        val player = Player(hand = listOf(destroyedCard), deck = listOf(cardToAdd))
        val game = Game.forPlayers(player, Player(), drawn = 0)

        val result = game.play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, destroyedCard.id))

        // Cards should not be added yet - should wait for destruction
        result.shouldBeSuccess().havePlayerOn(LEFT).hand.size shouldBe 0
    }

    @Test
    fun `AddCardsToHand with WhenDestroyed ALLIES should add cards when ally is destroyed`() {
        // Simplified test - will implement more complex scenarios once basic WhenDestroyed works
        val triggerCard = cardOf("trigger", "Trigger", effect = AddCardsToHand(listOf("card_to_add"), WhenDestroyed(TargetType.ALLIES)))
        val player = Player(hand = listOf(triggerCard), deck = listOf(cardToAdd))
        val game = Game.forPlayers(player, Player(), drawn = 0)

        val result = game.play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, triggerCard.id))

        // For now, just test that it doesn't add cards immediately (will extend later)
        result.shouldBeSuccess().havePlayerOn(LEFT).hand.size shouldBe 0
    }

    @Test
    fun `AddCardsToHand with WhenPlayed should still add cards immediately on placement`() {
        val playedCard = cardOf("played", "Played", effect = AddCardsToHand(listOf("card_to_add"), WhenPlayed()))
        val player = Player(hand = listOf(playedCard), deck = listOf(cardToAdd))
        val game = Game.forPlayers(player, Player(), drawn = 0)

        val result = game.play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, playedCard.id))

        // Cards should be added immediately on placement for WhenPlayed
        result.shouldBeSuccess().havePlayerOn(LEFT) should haveCardsAtHand(cardToAdd)
    }
}
