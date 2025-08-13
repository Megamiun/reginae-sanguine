package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ALLIES
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ANY
import br.com.gabryel.reginaesanguine.domain.effect.type.RaisePowerByCount
import br.com.gabryel.reginaesanguine.domain.helpers.A1
import br.com.gabryel.reginaesanguine.domain.helpers.B1
import br.com.gabryel.reginaesanguine.domain.helpers.B2
import br.com.gabryel.reginaesanguine.domain.helpers.B3
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.SECURITY_OFFICER
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test

class RaisePowerByCountTest {
    @Test
    fun `given effect scope doesn't match, when fetching raise amount, should not give any enhancement`() {
        val effect = RaisePowerByCount(scope = ALLIES, status = StatusType.ENHANCED, target = ANY)

        val summarizer = mockk<GameSummarizer> {
            every { getOccupiedCells() } returns mapOf(B1 to Cell(RIGHT, card = SECURITY_OFFICER))
            every { getExtraPowerAt(B1) } returns 5
        }

        val enhancement = effect.getDefaultAmount(summarizer, LEFT, A1, false)

        enhancement shouldBe 0
    }

    @Test
    fun `given effect status doesn't match, when fetching raise amount, should not give any enhancement`() {
        val effect = RaisePowerByCount(scope = ALLIES, status = StatusType.ENHANCED, target = ANY)

        val summarizer = mockk<GameSummarizer> {
            every { getOccupiedCells() } returns mapOf(B1 to Cell(LEFT, card = SECURITY_OFFICER))
            every { getExtraPowerAt(B1) } returns -5
        }

        val enhancement = effect.getDefaultAmount(summarizer, LEFT, A1, false)

        enhancement shouldBe 0
    }

    @Test
    fun `given effect status and scope match, when fetching raise amount, should give enhancement equal to number of matched cells`() {
        val effect = RaisePowerByCount(scope = ALLIES, status = StatusType.ENHANCED, target = ANY)

        val summarizer = mockk<GameSummarizer> {
            every { getOccupiedCells() } returns mapOf(
                A1 to Cell(LEFT, card = SECURITY_OFFICER),
                B1 to Cell(LEFT, card = SECURITY_OFFICER),
                B2 to Cell(RIGHT, card = SECURITY_OFFICER),
                B3 to Cell(LEFT, card = SECURITY_OFFICER),
            )
            every { getExtraPowerAt(A1) } returns 5
            every { getExtraPowerAt(B1) } returns 5
            every { getExtraPowerAt(B2) } returns 5
            every { getExtraPowerAt(B3) } returns -5
        }

        val enhancement = effect.getDefaultAmount(summarizer, LEFT, A1, false)

        enhancement shouldBe 2
    }

    @Test
    fun `given card target doesn't match, when fetching raise amount, should return 0`() {
        val effect = RaisePowerByCount(scope = ALLIES, status = StatusType.ENHANCED, target = ALLIES)

        val summarizer = mockk<GameSummarizer> {
            every { getOccupiedCells() } returns mapOf(A1 to Cell(LEFT, card = SECURITY_OFFICER))
            every { getExtraPowerAt(A1) } returns 5
        }

        val enhancement = effect.getRaiseBy(summarizer, LEFT, RIGHT, A1, false)

        enhancement shouldBe 0
    }
}
