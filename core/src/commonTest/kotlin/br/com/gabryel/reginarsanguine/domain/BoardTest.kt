package br.com.gabryel.reginarsanguine.domain

import br.com.gabryel.reginarsanguine.domain.Action.Play
import br.com.gabryel.reginarsanguine.domain.Card.*
import br.com.gabryel.reginarsanguine.domain.Failure.*
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginarsanguine.domain.matchers.*
import br.com.gabryel.reginarsanguine.util.buildResult
import kotlin.test.Test

class BoardTest {
    @Test
    fun `when playing a card on a valid position, should add player card to position`() {
        val nextBoard =
            Board.default()
                .play(LEFT, Play(1 to 0, SECURITY_OFFICER))

        nextBoard shouldBeSuccessfulAnd haveCell(1 to 0, cardCellWith(LEFT, SECURITY_OFFICER))
    }

    @Test
    fun `when playing a card on a position where you have no enough pins, should fail with NotEnoughPins`() {
        val nextBoard =
            Board.default()
                .play(LEFT, Play(1 to 0, RIOT_TROOPER))

        nextBoard.shouldBeFailure<NotEnoughPins>()
    }

    @Test
    fun `when playing a card on a position you have no control, should fail with CellDoesNotBelongToPlayer`() {
        val nextBoard =
            Board.default()
                .play(RIGHT, Play(1 to 0, SECURITY_OFFICER))

        nextBoard.shouldBeFailure<CellDoesNotBelongToPlayer>()
    }

    @Test
    fun `when playing a card on a position outside the board, should fail with OutOfBoard`() {
        val nextBoard =
            Board.default()
                .play(RIGHT, Play(-1 to -1, SECURITY_OFFICER))

        nextBoard.shouldBeFailure<OutOfBoard>()
    }

    @Test
    fun `when playing a card on a position where you already have a card, should fail with CellOccupied`() {
        val nextBoard =
            buildResult {
                Board.default()
                    .play(LEFT, Play(1 to 0, SECURITY_OFFICER)).orRaiseError()
                    .play(RIGHT, Play(1 to 4, SECURITY_OFFICER)).orRaiseError()
                    .play(LEFT, Play(1 to 0, SECURITY_OFFICER)).orRaiseError()
            }

        nextBoard.shouldBeFailure<CellOccupied>()
    }

    @Test
    fun `when playing a card, should increment pins on all increment positions described in the cards`() {
        val nextBoard =
            Board.default()
                .play(LEFT, Play(1 to 0, SECURITY_OFFICER))

        nextBoard shouldBeSuccessfulAnd
            haveCells(
                (0 to 0) to emptyCellOwnedBy(LEFT, 2),
                (2 to 0) to emptyCellOwnedBy(LEFT, 2),
                (1 to 1) to emptyCellOwnedBy(LEFT, 1),
                // Some extras for security
                (0 to 1) to unclaimedCell(),
                (2 to 1) to unclaimedCell(),
            )
    }
}
