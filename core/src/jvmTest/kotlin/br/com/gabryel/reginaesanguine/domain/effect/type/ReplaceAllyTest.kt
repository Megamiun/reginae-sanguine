package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Failure.CellDoesNotBelongToPlayer
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.effect.type.ReplaceAlly
import br.com.gabryel.reginaesanguine.domain.helpers.A1
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.cardOf
import br.com.gabryel.reginaesanguine.domain.matchers.shouldBeFailure
import br.com.gabryel.reginaesanguine.domain.matchers.shouldBeSuccess
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ReplaceAllyTest {
    @Test
    fun `ReplaceAlly should replace existing ally and provide power bonus based on replaced card`() {
        // Create original ally card with 2 power
        val originalAlly = cardOf(
            id = "original",
            name = "Original Ally",
            power = 2,
            rank = 1,
        )

        // Create ReplaceAlly card with powerMultiplier = 1 (should get 2 * 1 = 2 bonus power)
        val replaceCard = cardOf(
            id = "replace",
            name = "Replace Card",
            power = 1,
            rank = 1,
            effect = ReplaceAlly(powerMultiplier = 1),
        )

        val leftPlayer = Player(hand = listOf(originalAlly, replaceCard))
        val rightPlayer = Player(hand = emptyList())
        val game = Game.forPlayers(leftPlayer, rightPlayer, drawn = 0)

        // First, place the original ally
        val afterFirstPlay = game.play(LEFT, Play(A1, originalAlly.id))
            .shouldBeSuccess()

        // Verify original ally is placed with its base power
        afterFirstPlay.getTotalScoreAt(A1).orNull() shouldBe 2

        // Now replace the ally with the ReplaceAlly card
        val result = afterFirstPlay.play(LEFT, Play(A1, replaceCard.id))
            .shouldBeSuccess()

        // The replaced card should have: base power (1) + bonus from replaced ally (2 * 1) = 3
        result.getTotalScoreAt(A1).orNull() shouldBe 3

        // Verify the original ally is gone and replaced with the new card
        val cellAfterReplace = result.getCellAt(A1).orNull()!!
        cellAfterReplace.card?.id shouldBe "replace"
        cellAfterReplace.owner shouldBe LEFT
    }

    @Test
    fun `ReplaceAlly with powerMultiplier = 2 should double the replaced card power`() {
        val originalAlly = cardOf(
            id = "original",
            power = 3,
            rank = 1,
        )

        val replaceCard = cardOf(
            id = "replace",
            power = 1,
            rank = 1,
            effect = ReplaceAlly(powerMultiplier = 2),
        )

        val leftPlayer = Player(hand = listOf(originalAlly, replaceCard))
        val game = Game.forPlayers(leftPlayer, Player(), drawn = 0)

        val afterFirstPlay = game.play(LEFT, Play(A1, originalAlly.id))
            .shouldBeSuccess()

        val result = afterFirstPlay.play(LEFT, Play(A1, replaceCard.id))
            .shouldBeSuccess()

        // Base power (1) + bonus (3 * 2) = 7
        result.getTotalScoreAt(A1).orNull() shouldBe 7
    }

    @Test
    fun `ReplaceAlly with negative powerMultiplier should reduce power`() {
        val originalAlly = cardOf(
            id = "original",
            power = 4,
            rank = 1,
        )

        val replaceCard = cardOf(
            id = "replace",
            power = 2,
            rank = 1,
            effect = ReplaceAlly(powerMultiplier = -1),
        )

        val leftPlayer = Player(hand = listOf(originalAlly, replaceCard))
        val game = Game.forPlayers(leftPlayer, Player(), drawn = 0)

        val afterFirstPlay = game.play(LEFT, Play(A1, originalAlly.id))
            .shouldBeSuccess()

        val result = afterFirstPlay.play(LEFT, Play(A1, replaceCard.id))
            .shouldBeSuccess()

        // Base power (2) + bonus (4 * -1) = -2, but minimum should still be the base power behavior
        result.getTotalScoreAt(A1).orNull() shouldBe -2
    }

    @Test
    fun `ReplaceAlly with powerMultiplier = 0 should ignore replaced card power`() {
        val originalAlly = cardOf(
            id = "original",
            power = 5,
            rank = 1,
        )

        val replaceCard = cardOf(
            id = "replace",
            power = 3,
            rank = 1,
            effect = ReplaceAlly(powerMultiplier = 0),
        )

        val leftPlayer = Player(hand = listOf(originalAlly, replaceCard))
        val game = Game.forPlayers(leftPlayer, Player(), drawn = 0)

        val afterFirstPlay = game.play(LEFT, Play(A1, originalAlly.id))
            .shouldBeSuccess()

        val result = afterFirstPlay.play(LEFT, Play(A1, replaceCard.id))
            .shouldBeSuccess()

        // Base power (3) + bonus (5 * 0) = 3
        result.getTotalScoreAt(A1).orNull() shouldBe 3
    }

    @Test
    fun `ReplaceAlly should fail when trying to replace on empty cell`() {
        val replaceCard = cardOf(
            id = "replace",
            power = 1,
            rank = 1,
            effect = ReplaceAlly(powerMultiplier = 1),
        )

        val leftPlayer = Player(hand = listOf(replaceCard))
        val game = Game.forPlayers(leftPlayer, Player(), drawn = 0)

        // Try to play ReplaceAlly on empty cell - should fail
        val result = game.play(LEFT, Play(A1, replaceCard.id))

        result.shouldBeFailure<CellDoesNotBelongToPlayer>()
    }
}
