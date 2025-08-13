package br.com.gabryel.reginaesanguine.domain

import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.effect.EffectApplicationResult
import br.com.gabryel.reginaesanguine.domain.effect.EffectRegistry
import br.com.gabryel.reginaesanguine.domain.effect.None
import br.com.gabryel.reginaesanguine.domain.effect.StatusType
import br.com.gabryel.reginaesanguine.domain.effect.TargetType
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ALLIES
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ANY
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ENEMIES
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.SELF
import br.com.gabryel.reginaesanguine.domain.effect.WhenDestroyed
import br.com.gabryel.reginaesanguine.domain.effect.WhenFirstReachesPower
import br.com.gabryel.reginaesanguine.domain.effect.WhenFirstStatusChanged
import br.com.gabryel.reginaesanguine.domain.effect.WhenLaneWon
import br.com.gabryel.reginaesanguine.domain.effect.WhenPlayed
import br.com.gabryel.reginaesanguine.domain.effect.type.AddCardsToHand
import br.com.gabryel.reginaesanguine.domain.effect.type.NoEffect
import br.com.gabryel.reginaesanguine.domain.effect.type.PlayerModification
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
        val modifications = PlayerModification(cardsToAdd = cards)

        TargetType.entries.forEach { scope ->
            When("placing a PlayerEffect with WhenPlayed $scope") {
                val effect = AddCardsToHand(cards, WhenPlayed(scope))
                val firstResult = registry.onPlaceCard(LEFT, effect, A1, initBoard)
                val firstRegistry = firstResult.effectRegistry

                ThenValidatePlayerModifications(scope, firstResult, modifications, listOf(SELF))

                And("placing any ally card") {
                    val newBoard = board(LEFT to A1, LEFT to A2)
                    val result = firstRegistry.onPlaceCard(LEFT, NoEffect, A2, newBoard)

                    ThenValidatePlayerModifications(scope, result, modifications, listOf(ALLIES, ANY))
                }

                And("placing any enemy card") {
                    val newBoard = board(LEFT to A1, RIGHT to A2)
                    val result = firstRegistry.onPlaceCard(RIGHT, NoEffect, A2, newBoard)

                    ThenValidatePlayerModifications(scope, result, modifications, listOf(ENEMIES, ANY))
                }
            }
        }

        listOf(
            WhenDestroyed(SELF),
            WhenFirstStatusChanged(StatusType.ANY),
            WhenFirstReachesPower(1),
            WhenLaneWon,
            None,
        ).forEach { trigger ->
            When("placing a PlayerEffect with $trigger") {
                val effect = AddCardsToHand(cards, trigger)
                val result = registry.onPlaceCard(LEFT, effect, A1, initBoard)

                Then("NOT return PlayerModifications") { result.playerModifications.shouldBeEmpty() }
            }
        }
    }
})

@Kotest
private suspend fun BehaviorSpecWhenContainerScope.ThenValidatePlayerModifications(
    scope: TargetType,
    result: EffectApplicationResult,
    modifications: PlayerModification,
    allowed: List<TargetType>
) {
    when (scope) {
        in allowed -> Then("return extra cards on PlayerModification") {
            result.playerModifications shouldContainAll mapOf(LEFT to modifications)
        }

        else -> Then("return no cards on PlayerModification") {
            result.playerModifications.shouldBeEmpty()
        }
    }
}

private fun board(vararg cards: Pair<PlayerPosition, Position>) = Board(
    cards.associate { (player, position) -> position to Cell(player, 0, SECURITY_OFFICER) },
)
