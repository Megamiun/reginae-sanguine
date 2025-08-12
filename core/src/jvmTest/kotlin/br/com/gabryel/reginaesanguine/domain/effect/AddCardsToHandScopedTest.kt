package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.Action.Play
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

class AddCardsToHandScopedTest {
    private val cardToAdd = cardOf("reward", "Reward Card")

    @Test
    fun `AddCardsToHand with WhenPlayed SELF should only trigger when itself is played`() {
        val triggerCard = cardOf("trigger", "Trigger", effect = AddCardsToHand(listOf("reward"), WhenPlayed(TargetType.SELF)))
        val player = Player(hand = listOf(triggerCard), deck = listOf(cardToAdd))
        val game = Game.forPlayers(player, Player(), drawn = 0)

        val result = game.play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, triggerCard.id))

        // Should add cards because the trigger card itself was played (SELF scope)
        result.shouldBeSuccess().havePlayerOn(LEFT) should haveCardsAtHand(cardToAdd)
    }

    @Test
    fun `AddCardsToHand with WhenPlayed ALLIES should NOT trigger when no allies are played`() {
        val triggerCard = cardOf("trigger", "Trigger", effect = AddCardsToHand(listOf("reward"), WhenPlayed(TargetType.ALLIES)))
        val player = Player(hand = listOf(triggerCard), deck = listOf(cardToAdd))
        val game = Game.forPlayers(player, Player(), drawn = 0)

        val result = game.play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, triggerCard.id))

        // Should NOT add cards because no allies were played, only the trigger card itself
        result.shouldBeSuccess().havePlayerOn(LEFT).hand.size shouldBe 0
    }

    @Test
    fun `AddCardsToHand with WhenPlayed ALLIES should trigger when ally is played after trigger card`() {
        val triggerCard = cardOf("trigger", "Trigger", effect = AddCardsToHand(listOf("reward"), WhenPlayed(TargetType.ALLIES)))
        val allyCard = cardOf("ally", "Ally Card")
        val rightCard = cardOf("right", "Right Card")

        val leftPlayer = Player(hand = listOf(triggerCard, allyCard), deck = listOf(cardToAdd))
        val rightPlayer = Player(hand = listOf(rightCard))
        val game = Game.forPlayers(leftPlayer, rightPlayer, drawn = 0)

        // Place trigger card first (LEFT's turn)
        val afterTrigger = game.play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, triggerCard.id))
            .shouldBeSuccess()

        // Should not have cards yet
        afterTrigger.havePlayerOn(LEFT).hand.size shouldBe 1 // Only ally card remaining

        // RIGHT's turn - place a card
        val afterRight = afterTrigger.play(RIGHT, Play(MIDDLE_LANE atColumn RIGHT_COLUMN, rightCard.id))
            .shouldBeSuccess()

        // Now LEFT's turn again - Place ally card (same player as trigger)
        val result = afterRight.play(LEFT, Play((MIDDLE_LANE + 1) atColumn LEFT_COLUMN, allyCard.id))

        // Should add reward card to LEFT player because ally was played
        result.shouldBeSuccess().havePlayerOn(LEFT) should haveCardsAtHand(cardToAdd)
    }

    @Test
    fun `AddCardsToHand with WhenPlayed ENEMIES should trigger when enemy is played`() {
        val triggerCard = cardOf("trigger", "Trigger", effect = AddCardsToHand(listOf("reward"), WhenPlayed(TargetType.ENEMIES)))
        val enemyCard = cardOf("enemy", "Enemy Card")

        val leftPlayer = Player(hand = listOf(triggerCard), deck = listOf(cardToAdd))
        val rightPlayer = Player(hand = listOf(enemyCard))
        val game = Game.forPlayers(leftPlayer, rightPlayer, drawn = 0)

        // Place trigger card first
        val afterTrigger = game.play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, triggerCard.id))
            .shouldBeSuccess()

        // Should not have cards yet
        afterTrigger.havePlayerOn(LEFT).hand.size shouldBe 0

        // RIGHT player places enemy card
        val result = afterTrigger.play(RIGHT, Play(MIDDLE_LANE atColumn RIGHT_COLUMN, enemyCard.id))

        // Should add reward card to LEFT player because enemy was played
        result.shouldBeSuccess().havePlayerOn(LEFT) should haveCardsAtHand(cardToAdd)
    }

    @Test
    fun `AddCardsToHand with WhenPlayed ANY should trigger when any card is played`() {
        val triggerCard = cardOf("trigger", "Trigger", effect = AddCardsToHand(listOf("reward"), WhenPlayed(TargetType.ANY)))
        val otherCard = cardOf("other", "Other Card")

        val leftPlayer = Player(hand = listOf(triggerCard), deck = listOf(cardToAdd))
        val rightPlayer = Player(hand = listOf(otherCard))
        val game = Game.forPlayers(leftPlayer, rightPlayer, drawn = 0)

        // Place trigger card first
        val afterTrigger = game.play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, triggerCard.id))
            .shouldBeSuccess()

        // Should not have cards yet because ANY excludes SELF
        afterTrigger.havePlayerOn(LEFT).hand.size shouldBe 0

        // RIGHT player places other card
        val result = afterTrigger.play(RIGHT, Play(MIDDLE_LANE atColumn RIGHT_COLUMN, otherCard.id))

        // Should add reward card to LEFT player because any other card was played
        result.shouldBeSuccess().havePlayerOn(LEFT) should haveCardsAtHand(cardToAdd)
    }
}
