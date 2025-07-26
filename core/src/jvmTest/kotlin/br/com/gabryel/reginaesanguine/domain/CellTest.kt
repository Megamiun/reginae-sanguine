package br.com.gabryel.reginaesanguine.domain

import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.SECURITY_OFFICER
import br.com.gabryel.reginaesanguine.domain.matchers.cardCellWith
import br.com.gabryel.reginaesanguine.domain.matchers.emptyCellOwnedBy
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CellTest {
    @Test
    fun `when incrementing cell, should not increment ranks over 3`() {
        val updatedCell = Cell.EMPTY.increment(LEFT, 4)

        updatedCell shouldBe emptyCellOwnedBy(LEFT, 3)
    }

    @Test
    fun `when incrementing empty cell owned by opponent, should steal it but not increment`() {
        val updatedCell = Cell(RIGHT, 1).increment(LEFT, 1)

        updatedCell shouldBe emptyCellOwnedBy(LEFT, 1)
    }

    @Test
    fun `when incrementing card cell, should not increment ranks`() {
        val updatedCell = Cell(RIGHT, 1, SECURITY_OFFICER).increment(LEFT, 1)

        updatedCell shouldBe cardCellWith(RIGHT, SECURITY_OFFICER, 1)
    }
}
