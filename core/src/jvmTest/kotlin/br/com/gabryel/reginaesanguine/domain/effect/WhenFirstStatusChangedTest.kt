package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.effect.StatusType.ENHANCED
import br.com.gabryel.reginaesanguine.domain.helpers.A1
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test

class WhenFirstStatusChangedTest {
    @Test
    fun `given card has no status, when checking if trigger satisfied, should return false`() {
        val trigger = WhenFirstStatusChanged(ENHANCED)

        val summarizer = mockk<GameSummarizer> {
            every { getExtraPowerAt(A1) } returns 0
        }

        trigger.isSatisfied(summarizer, A1) shouldBe false
    }

    @Test
    fun `given card has correct status, when checking if trigger satisfied, should return true`() {
        val trigger = WhenFirstStatusChanged(ENHANCED)

        val summarizer = mockk<GameSummarizer> {
            every { getExtraPowerAt(A1) } returns 1
        }

        trigger.isSatisfied(summarizer, A1) shouldBe true
    }
}
