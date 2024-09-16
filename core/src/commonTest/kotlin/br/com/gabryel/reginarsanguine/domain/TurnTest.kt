package br.com.gabryel.reginarsanguine.domain

import br.com.gabryel.reginarsanguine.domain.Card.SECURITY_OFFICER
import br.com.gabryel.reginarsanguine.domain.Failure.NotPlayerTurn
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.RIGHT
import io.kotest.matchers.equals.shouldBeEqual
import kotlin.test.Test

class TurnTest {
    @Test
    fun `when playing a card on a valid position should add player card to position`() {
        val nextTurn = Turn.default()
            .play(LEFT, Action.Play(1, 0, SECURITY_OFFICER))

        nextTurn.flatmap { it.getCellAt(1, 0) }
            .shouldBeSuccessAndBe(cellWith(LEFT, SECURITY_OFFICER))
    }

    @Test
    fun `when playing a card should change player`() {
        val nextTurn = Turn.default()
            .play(LEFT, Action.Play(1, 0, SECURITY_OFFICER))

        nextTurn
            .shouldBeSuccess()
            .nextPlayer.shouldBeEqual(RIGHT)
    }

    @Test
    fun `when playing a card as the wrong player, should fail with NotPlayerTurn`() {
        val nextTurn = Turn.default()
            .play(RIGHT, Action.Play(1, 0, SECURITY_OFFICER))

        nextTurn.shouldBeFailure<NotPlayerTurn>()
    }

    // TODO when making two consecutive skips, should end game
}

