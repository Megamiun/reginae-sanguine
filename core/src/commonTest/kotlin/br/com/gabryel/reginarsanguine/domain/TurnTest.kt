package br.com.gabryel.reginarsanguine.domain

import br.com.gabryel.reginarsanguine.domain.Card.SECURITY_OFFICER
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.RIGHT
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.should
import kotlin.test.Test

class TurnTest {
    @Test
    fun `when making a move on a valid position should add player card to position`() {
        val nextTurn = Turn.default().play(LEFT, Action.Play(0, 0, SECURITY_OFFICER))

        nextTurn.at(0, 0) should beCellWith(LEFT, SECURITY_OFFICER)
    }

    @Test
    fun `when making a move should change player`() {
        val nextTurn = Turn.default().play(LEFT, Action.Play(0, 0, SECURITY_OFFICER))

        nextTurn.nextPlayer shouldBeEqual RIGHT
    }

    // TODO when making a move as the wrong player, should fail
}

