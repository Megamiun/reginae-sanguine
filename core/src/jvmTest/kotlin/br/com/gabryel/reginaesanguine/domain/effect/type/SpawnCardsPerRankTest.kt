package br.com.gabryel.reginaesanguine.domain.effect.type

import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.effect.GameSummarizer
import br.com.gabryel.reginaesanguine.domain.effect.WhenPlayed
import br.com.gabryel.reginaesanguine.domain.helpers.A1
import br.com.gabryel.reginaesanguine.domain.helpers.A2
import br.com.gabryel.reginaesanguine.domain.helpers.A3
import br.com.gabryel.reginaesanguine.domain.helpers.B1
import br.com.gabryel.reginaesanguine.domain.helpers.B2
import br.com.gabryel.reginaesanguine.domain.helpers.B3
import io.kotest.matchers.maps.shouldContainAll
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test

class SpawnCardsPerRankTest {
    @Test
    fun `when playing a SpawnCardsPerRank card, should correct fill all player owned empty cells`() {
        val effect = SpawnCardsPerRank(listOf("rank1", "rank2", "rank3"), WhenPlayed())

        val board = mockk<GameSummarizer> {
            every { getOwnedCells() } returns mapOf(
                A1 to Cell(LEFT, 1),
                A2 to Cell(LEFT, 2),
                A3 to Cell(LEFT, 3),
                B1 to Cell(RIGHT, 1),
                B2 to Cell(RIGHT, 2),
                B3 to Cell(RIGHT, 3),
            )
        }

        effect.getSpawns(board, LEFT) shouldContainAll mapOf(
            LEFT to mapOf(A1 to "rank1", A2 to "rank2", A3 to "rank3"),
        )
    }
}
