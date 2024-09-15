package br.com.gabryel.reginarsanguine.domain

import br.com.gabryel.reginarsanguine.domain.Card.SECURITY_OFFICER
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.LEFT
import io.kotest.matchers.should
import kotlin.test.Test

class BoardTest {
    @Test
    fun `when making a move on a valid position should add player card to position`() {
        val nextTurn = Board.default().play(LEFT, Action.Play(0, 0, SECURITY_OFFICER))

        nextTurn.at(0, 0) should beCellWith(LEFT, SECURITY_OFFICER)
    }

    // TODO when making a move, should increment pins on all increment positions
    // TODO when making a move on a position where you have no enough pins, should fail
    // TODO when making a move on a position where the other player has control, should fail
    // TODO when making a move on a position where you have no enough pins, should fail
    // TODO when making a move on a position outside the board, should fail

    // TODO when making a move, should increment pins on all increment positions
    // TODO when making a move, should not increment pins over 3
}


