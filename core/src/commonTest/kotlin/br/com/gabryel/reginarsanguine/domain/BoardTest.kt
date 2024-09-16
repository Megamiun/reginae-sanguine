package br.com.gabryel.reginarsanguine.domain

import br.com.gabryel.reginarsanguine.domain.Action.Play
import br.com.gabryel.reginarsanguine.domain.Card.RIOT_TROOPER
import br.com.gabryel.reginarsanguine.domain.Card.SECURITY_OFFICER
import br.com.gabryel.reginarsanguine.domain.Failure.*
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.RIGHT
import kotlin.test.Test

class BoardTest {
    @Test
    fun `when playing a card on a valid position, should add player card to position`() {
        val nextTurn = Board.default()
            .play(LEFT, Play(1, 0, SECURITY_OFFICER))

        nextTurn.flatmap { it.getCellAt(1, 0) }
            .shouldBeSuccessAndBe(cellWith(LEFT, SECURITY_OFFICER))
    }

    @Test
    fun `when playing a card on a position where you have no enough pins, should fail with NotEnoughPins`() {
        val nextTurn = Board.default()
            .play(LEFT, Play(1, 0, RIOT_TROOPER))

        nextTurn.shouldBeFailure<NotEnoughPins>()
    }

    @Test
    fun `when playing a card on a position where he has no control, should fail with DoesNotBelongToPlayer`() {
        val nextTurn = Board.default()
            .play(RIGHT, Play(1, 0, SECURITY_OFFICER))

        nextTurn.shouldBeFailure<DoesNotBelongToPlayer>()
    }

    @Test
    fun `when playing a card on a position outside the board, should fail with CellOutsideOfBoard`() {
        val nextTurn = Board.default()
            .play(RIGHT, Play(-1, -1, SECURITY_OFFICER))

        nextTurn.shouldBeFailure<CellOutsideOfBoard>()
    }

    // TODO when playing a card, should increment pins on all increment positions
    // TODO when playing a card, should not increment pins over 3

    // TODO when playing a pin card on a position where you already have a monster, should fail
}


