package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.effect.type.ScoreBonus
import br.com.gabryel.reginaesanguine.domain.helpers.A1
import br.com.gabryel.reginaesanguine.domain.helpers.TOP_LANE
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class ScoreBonusTest {
    @Test
    fun `given player wins the current lane, when fetching Lane Raise amounts, should return extra amount on lane`() {
        val effect = ScoreBonus(5)

        val summarizer = mockk<GameSummarizer> {
            every { getBaseLaneScoreAt(TOP_LANE) } returns mapOf(LEFT to 3, RIGHT to 2)
        }

        val raiseLaneAmounts = effect.getRaiseLaneAmounts(summarizer, LEFT, A1)
        raiseLaneAmounts shouldContainExactly mapOf(TOP_LANE to mapOf(LEFT to 5))
    }

    @Test
    fun `given player loses the current lane, when fetching Lane Raise amounts, should return nothing`() {
        val effect = ScoreBonus(5)

        val summarizer = mockk<GameSummarizer> {
            every { getBaseLaneScoreAt(TOP_LANE) } returns mapOf(LEFT to 2, RIGHT to 3)
        }

        val raiseLaneAmounts = effect.getRaiseLaneAmounts(summarizer, LEFT, A1)
        raiseLaneAmounts shouldHaveSize 0
    }

    @Test
    fun `given player ties the current lane, when fetching Lane Raise amounts, should return nothing`() {
        val effect = ScoreBonus(5)

        val summarizer = mockk<GameSummarizer> {
            every { getBaseLaneScoreAt(TOP_LANE) } returns mapOf(LEFT to 2, RIGHT to 2)
        }

        val raiseLaneAmounts = effect.getRaiseLaneAmounts(summarizer, LEFT, A1)
        raiseLaneAmounts shouldHaveSize 0
    }
}
