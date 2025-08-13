package br.com.gabryel.reginaesanguine.domain.effect.type

import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.effect.GameSummarizer
import br.com.gabryel.reginaesanguine.domain.helpers.A1
import br.com.gabryel.reginaesanguine.domain.helpers.BOTTOM_LANE
import br.com.gabryel.reginaesanguine.domain.helpers.MIDDLE_LANE
import br.com.gabryel.reginaesanguine.domain.helpers.TOP_LANE
import io.kotest.matchers.maps.shouldContainExactly
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class RaiseWinnerLanesByLoserScoreTest {
    @Test
    fun `given player wins any lane, when fetching Lane Raise amounts, should return extra amount on lane`() {
        val summarizer = mockk<GameSummarizer> {
            every { getBaseLaneScoreAt(TOP_LANE) } returns mapOf(LEFT to 3, RIGHT to 2)
            every { getBaseLaneScoreAt(MIDDLE_LANE) } returns mapOf(LEFT to 4, RIGHT to 5)
            every { getBaseLaneScoreAt(BOTTOM_LANE) } returns mapOf(LEFT to 6, RIGHT to 6)
        }

        val raiseLaneAmounts = RaiseWinnerLanesByLoserScore()
            .getRaiseLaneAmounts(summarizer, LEFT, A1)

        raiseLaneAmounts shouldContainExactly mapOf(
            TOP_LANE to mapOf(LEFT to 2),
            MIDDLE_LANE to mapOf(RIGHT to 4),
            BOTTOM_LANE to emptyMap(),
        )
    }
}
