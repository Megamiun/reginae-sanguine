package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.Board
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ALLIES
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ANY
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ENEMIES
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.SELF
import br.com.gabryel.reginaesanguine.domain.effect.type.AddCardsToHandDefault
import br.com.gabryel.reginaesanguine.domain.effect.type.NoEffect
import br.com.gabryel.reginaesanguine.domain.helpers.A1
import br.com.gabryel.reginaesanguine.domain.helpers.A2
import br.com.gabryel.reginaesanguine.domain.helpers.Kotest
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.SECURITY_OFFICER
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecWhenContainerScope
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainAll

class EffectRegistryPlayerEffectTest : BehaviorSpec({
    val initBoard = board(LEFT to A1)

    Given("an empty registry") {
        val registry = EffectRegistry()
        val cards = listOf("card1")

        TargetType.entries.forEach { scope ->
            When("placing a PlayerEffect with WhenPlayed $scope") {
                val effect = AddCardsToHandDefault(cards, WhenPlayed(scope))
                val firstResult = registry.onPlaceCard(LEFT, effect, A1, initBoard)
                val firstRegistry = firstResult.effectRegistry

                ThenValidatePlayerModifications(scope, firstResult, cards, listOf(SELF))

                And("placing any ally card") {
                    val newBoard = board(LEFT to A1, LEFT to A2)
                    val result = firstRegistry.onPlaceCard(LEFT, NoEffect, A2, newBoard)

                    ThenValidatePlayerModifications(scope, result, cards, listOf(ALLIES, ANY))
                }

                And("placing any enemy card") {
                    val newBoard = board(LEFT to A1, RIGHT to A2)
                    val result = firstRegistry.onPlaceCard(RIGHT, NoEffect, A2, newBoard)

                    ThenValidatePlayerModifications(scope, result, cards, listOf(ENEMIES, ANY))
                }
            }
        }

        // TODO Check about adding more specific test cases
        listOf(
            WhenDestroyed(SELF),
            WhenFirstStatusChanged(StatusType.ANY),
            WhenFirstReachesPower(1),
            WhenLaneWon,
            None,
        ).forEach { trigger ->
            When("placing a PlayerEffect with $trigger") {
                val effect = AddCardsToHandDefault(cards, trigger)
                val result = registry.onPlaceCard(LEFT, effect, A1, initBoard)

                Then("NOT return PlayerModifications") { result.toAddToHand.shouldBeEmpty() }
            }
        }
    }
})

@Kotest
private suspend fun BehaviorSpecWhenContainerScope.ThenValidatePlayerModifications(
    scope: TargetType,
    result: EffectApplicationResult,
    toAddToHand: List<String>,
    allowed: List<TargetType>
) {
    when (scope) {
        in allowed -> Then("return extra cards on PlayerModification") {
            result.toAddToHand shouldContainAll mapOf(LEFT to toAddToHand)
        }

        else -> Then("return no cards on PlayerModification") {
            result.toAddToHand.shouldBeEmpty()
        }
    }
}

private fun board(vararg cards: Pair<PlayerPosition, Position>) = Board(
    cards.associate { (player, position) -> position to Cell(player, 0, SECURITY_OFFICER) },
)
