package br.com.gabryel.reginaesanguine.domain.effect.type

import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.effect.GameSummarizer
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ALLIES
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ANY
import br.com.gabryel.reginaesanguine.domain.helpers.A1
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test

private const val ENHANCED_AMOUNT = 5
private const val ENFEEBLED_AMOUNT = 1

class RaisePowerOnStatusTest {
    @Test
    fun `given card is enhanced, when fetching default amount, should return enhancementAmount`() {
        val effect = RaisePowerOnStatus(ENHANCED_AMOUNT, ENFEEBLED_AMOUNT, ANY)

        val summarizer = mockk<GameSummarizer> {
            every { getExtraPowerAt(A1) } returns 1
        }

        val enhancement = effect.getDefaultAmount(summarizer, LEFT, A1, false)

        enhancement shouldBe ENHANCED_AMOUNT
    }

    @Test
    fun `given card is enfeebled, when fetching default amount should return enfeebledAmount`() {
        val effect = RaisePowerOnStatus(ENHANCED_AMOUNT, ENFEEBLED_AMOUNT, ANY)

        val summarizer = mockk<GameSummarizer> {
            every { getExtraPowerAt(A1) } returns -1
        }

        val enhancement = effect.getDefaultAmount(summarizer, LEFT, A1, false)

        enhancement shouldBe ENFEEBLED_AMOUNT
    }

    @Test
    fun `given card has no status, when fetching default amount should return 0`() {
        val effect = RaisePowerOnStatus(ENHANCED_AMOUNT, ENFEEBLED_AMOUNT, ANY)

        val summarizer = mockk<GameSummarizer> {
            every { getExtraPowerAt(A1) } returns 0
        }

        val enhancement = effect.getDefaultAmount(summarizer, LEFT, A1, false)

        enhancement shouldBe 0
    }

    @Test
    fun `given card target doesn't match, when fetching raise amount, should return 0`() {
        val effect = RaisePowerOnStatus(ENHANCED_AMOUNT, ENFEEBLED_AMOUNT, ALLIES)

        val summarizer = mockk<GameSummarizer> {
            every { getExtraPowerAt(A1) } returns 1
        }

        val enhancement = effect.getRaiseBy(summarizer, LEFT, RIGHT, A1, false)

        enhancement shouldBe 0
    }
}
