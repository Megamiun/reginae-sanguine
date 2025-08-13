package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.Success
import br.com.gabryel.reginaesanguine.domain.helpers.A1
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test

class WhenFirstReachesPowerTest {
    @Test
    fun `given card has less power than threshold, when checking if trigger satisfied, should return false`() {
        val trigger = WhenFirstReachesPower(7)

        val summarizer = mockk<GameSummarizer> {
            every { getTotalScoreAt(A1) } returns Success(6)
        }

        trigger.isSatisfied(summarizer, A1) shouldBe false
    }

    @Test
    fun `given card has correct status, when checking if trigger satisfied, should return true`() {
        val trigger = WhenFirstReachesPower(7)

        val summarizer = mockk<GameSummarizer> {
            every { getTotalScoreAt(A1) } returns Success(7)
        }

        trigger.isSatisfied(summarizer, A1) shouldBe true
    }
}
