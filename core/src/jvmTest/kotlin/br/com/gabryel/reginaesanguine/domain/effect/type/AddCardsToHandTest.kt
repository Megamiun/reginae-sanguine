package br.com.gabryel.reginaesanguine.domain.effect.type

import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.effect.GameSummarizer
import br.com.gabryel.reginaesanguine.domain.effect.WhenPlayed
import br.com.gabryel.reginaesanguine.domain.helpers.A1
import io.kotest.matchers.maps.shouldContainExactly
import io.mockk.mockk
import kotlin.test.Test

class AddCardsToHandTest {
    private val game = mockk<GameSummarizer>()

    @Test
    fun `AddCardsToHand should work with single card`() {
        val effect = AddCardsToHandDefault(listOf("single_card"), WhenPlayed())

        val result = effect.getNewCards(mockk(), LEFT, A1)

        result shouldContainExactly mapOf(LEFT to listOf("single_card"))
    }

    @Test
    fun `AddCardsToHand should return PlayerModification for source player`() {
        val effect = AddCardsToHandDefault(listOf("card1", "card2"), WhenPlayed())

        val result = effect.getNewCards(game, LEFT, A1)

        result shouldContainExactly mapOf(LEFT to listOf("card1", "card2"))
    }

    @Test
    fun `AddCardsToHand should return empty list for empty cardIds`() {
        val effect = AddCardsToHandDefault(emptyList(), WhenPlayed())

        val result = effect.getNewCards(game, LEFT, A1)

        result shouldContainExactly mapOf(LEFT to emptyList())
    }

    @Test
    fun `AddCardsToHand should work with RIGHT player`() {
        val effect = AddCardsToHandDefault(listOf("right_card"), WhenPlayed())

        val result = effect.getNewCards(game, RIGHT, A1)

        result shouldContainExactly mapOf(RIGHT to listOf("right_card"))
    }

    @Test
    fun `AddCardsToHand should allow duplicate card IDs`() {
        val effect = AddCardsToHandDefault(listOf("card1", "card1", "card2"), WhenPlayed())

        val result = effect.getNewCards(game, LEFT, A1)

        result shouldContainExactly mapOf(LEFT to listOf("card1", "card1", "card2"))
    }
}
