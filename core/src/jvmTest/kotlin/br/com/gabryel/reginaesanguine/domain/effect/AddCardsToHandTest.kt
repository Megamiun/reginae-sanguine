package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.effect.type.AddCardsToHand
import br.com.gabryel.reginaesanguine.domain.effect.type.PlayerModification
import br.com.gabryel.reginaesanguine.domain.helpers.A1
import io.kotest.matchers.maps.shouldContainExactly
import io.mockk.mockk
import kotlin.test.Test

class AddCardsToHandTest {
    private val mockGameSummarizer = mockk<GameSummarizer>()

    @Test
    fun `AddCardsToHand should work with single card`() {
        val effect = AddCardsToHand(listOf("single_card"), WhenPlayed())

        val result = effect.getPlayerModifications(mockGameSummarizer, LEFT, A1)

        result shouldContainExactly mapOf(
            LEFT to PlayerModification(cardsToAdd = listOf("single_card")),
        )
    }

    @Test
    fun `AddCardsToHand should return PlayerModification for source player`() {
        val effect = AddCardsToHand(listOf("card1", "card2"), WhenPlayed())

        val result = effect.getPlayerModifications(mockGameSummarizer, LEFT, A1)

        result shouldContainExactly mapOf(
            LEFT to PlayerModification(cardsToAdd = listOf("card1", "card2")),
        )
    }

    @Test
    fun `AddCardsToHand should return empty list for empty cardIds`() {
        val effect = AddCardsToHand(emptyList(), WhenPlayed())

        val result = effect.getPlayerModifications(mockGameSummarizer, LEFT, A1)

        result shouldContainExactly mapOf(
            LEFT to PlayerModification(cardsToAdd = emptyList()),
        )
    }

    @Test
    fun `AddCardsToHand should work with RIGHT player`() {
        val effect = AddCardsToHand(listOf("right_card"), WhenPlayed())

        val result = effect.getPlayerModifications(mockGameSummarizer, RIGHT, A1)

        result shouldContainExactly mapOf(
            RIGHT to PlayerModification(cardsToAdd = listOf("right_card")),
        )
    }

    @Test
    fun `AddCardsToHand should allow duplicate card IDs`() {
        val effect = AddCardsToHand(listOf("card1", "card1", "card2"), WhenPlayed())

        val result = effect.getPlayerModifications(mockGameSummarizer, LEFT, A1)

        result shouldContainExactly mapOf(
            LEFT to PlayerModification(cardsToAdd = listOf("card1", "card1", "card2")),
        )
    }
}
