package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Displacement
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

class AddCardsToHandDestructionIntegrationTest {
    @Test
    fun `AddCardsToHand with WhenDestroyed SELF should trigger when the card is destroyed by power reduction`() {
        val cardToAdd = cardOf("reward", "Reward Card")

        // Card with low power that will be destroyed and has WhenDestroyed effect
        val vulnerableCard = cardOf(
            id = "vulnerable",
            name = "Vulnerable Card",
            power = 1,
            effect = AddCardsToHand(listOf("reward"), WhenDestroyed()),
        )

        // Card that reduces power and destroys the vulnerable card
        val attackCard = cardOf(
            id = "attack",
            name = "Attack Card",
            increments = setOf(Displacement(-1, 0)), // Attack to the left
            effect = RaisePower(-2, TargetType.ANY, WhileActive, setOf(Displacement(-1, 0))),
        )

        val leftPlayer = Player(hand = listOf(vulnerableCard), deck = listOf(cardToAdd))
        val rightPlayer = Player(hand = listOf(attackCard))
        val game = Game.forPlayers(leftPlayer, rightPlayer, drawn = 0)

        // LEFT places vulnerable card
        val afterLeftPlay = game.play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, vulnerableCard.id))
            .shouldBeSuccess()

        // Verify card is not yet in hand
        afterLeftPlay.havePlayerOn(LEFT).hand.size shouldBe 0

        // RIGHT places attack card that should destroy the vulnerable card (1 power - 2 power reduction = destroyed)
        val result = afterLeftPlay.play(RIGHT, Play(MIDDLE_LANE atColumn RIGHT_COLUMN, attackCard.id))

        // Now the reward card should be added to LEFT player's hand due to destruction trigger
        result.shouldBeSuccess().havePlayerOn(LEFT) should haveCardsAtHand(cardToAdd)
    }
}
