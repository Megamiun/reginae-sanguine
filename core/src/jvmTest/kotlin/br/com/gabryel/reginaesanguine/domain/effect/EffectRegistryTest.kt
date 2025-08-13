package br.com.gabryel.reginaesanguine.domain

import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.RIGHTWARD
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.effect.EffectRegistry
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ALLIES
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ANY
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.SELF
import br.com.gabryel.reginaesanguine.domain.effect.WhenDestroyed
import br.com.gabryel.reginaesanguine.domain.effect.WhenPlayed
import br.com.gabryel.reginaesanguine.domain.effect.WhileActive
import br.com.gabryel.reginaesanguine.domain.effect.type.NoEffect
import br.com.gabryel.reginaesanguine.domain.effect.type.RaisePower
import br.com.gabryel.reginaesanguine.domain.helpers.A1
import br.com.gabryel.reginaesanguine.domain.helpers.A2
import br.com.gabryel.reginaesanguine.domain.helpers.A4
import br.com.gabryel.reginaesanguine.domain.helpers.A5
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.SECURITY_OFFICER
import br.com.gabryel.reginaesanguine.domain.matchers.havePositionExtraPower
import io.kotest.matchers.collections.containAll
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertAll
import kotlin.test.Test

class EffectRegistryTest {
    val defaultBoard = boardWithFilledCells(A1, A2, A4, A5)

    @Test
    fun `when a card with WhenPlayed trigger is placed, should add consider it on extra power calculation`() {
        val effect = RaisePower(1, ANY, WhileActive, setOf(RIGHTWARD))

        val result = EffectRegistry()
            .onPlaceCard(LEFT, effect, A1, defaultBoard)

        result.effectRegistry.getExtraPowerAt(A2, defaultBoard) shouldBe 1
    }

    @Test
    fun `when a card with WhenPlayed ALLY trigger is placed, should only consider played after another ally is played`() {
        val effect = RaisePower(1, SELF, WhenPlayed(ALLIES))

        val initial = EffectRegistry()
        val beforeDestroyed = initial.onPlaceCard(LEFT, effect, A1, defaultBoard).effectRegistry
        val afterDestroyed = beforeDestroyed.onPlaceCard(LEFT, NoEffect, A2, defaultBoard).effectRegistry

        assertAll(
            { beforeDestroyed should havePositionExtraPower(A1, 0, defaultBoard) },
            { afterDestroyed should havePositionExtraPower(A1, 1, defaultBoard) },
        )
    }

    @Test
    fun `when a card with WhenDestroyed SELF trigger is placed, should only consider deletion is listened`() {
        val effect = RaisePower(1, ANY, WhenDestroyed(), affected = setOf(RIGHTWARD))

        val initial = EffectRegistry()
        val beforeDestroyed = initial.onPlaceCard(LEFT, effect, A1, defaultBoard).effectRegistry
        val afterDestroyed = beforeDestroyed.onDestroy(setOf(A1), defaultBoard).effectRegistry

        assertAll(
            { beforeDestroyed should havePositionExtraPower(A2, 0, defaultBoard) },
            { afterDestroyed should havePositionExtraPower(A2, 1, defaultBoard) },
        )
    }

    @Test
    fun `when a card with WhenDestroyed ALLY trigger is placed, should only consider deletion after another ally is destroyed`() {
        val effect = RaisePower(1, SELF, WhenDestroyed(ALLIES))

        val initial = EffectRegistry()
        val beforeDestroyed = initial.onPlaceCard(LEFT, effect, A1, defaultBoard).effectRegistry
        val afterDestroyed = beforeDestroyed.onDestroy(setOf(A2), defaultBoard).effectRegistry

        assertAll(
            { beforeDestroyed should havePositionExtraPower(A1, 0, defaultBoard) },
            { afterDestroyed should havePositionExtraPower(A1, 1, defaultBoard) },
        )
    }

    @Test
    fun `when a card is destroyed, should clean all applied effects on position`() {
        val effect = RaisePower(1, ANY, WhileActive, affected = setOf(RIGHTWARD))

        val afterDestroyed = EffectRegistry()
            .onPlaceCard(LEFT, effect, A1, defaultBoard).effectRegistry
            .onDestroy(setOf(A1), defaultBoard).effectRegistry

        afterDestroyed should havePositionExtraPower(A1, 0, defaultBoard)
    }

    // TODO when a card with WhenFirstStatusChanged trigger is placed, should only consider after condition triggered
    // TODO when a card with WhenFirstReachesPower trigger is placed, should only consider after condition triggered

    @Test
    fun `when a effect is played by RIGHT player, should apply effect to mirrored position`() {
        val effect = RaisePower(1, ANY, WhileActive, affected = setOf(RIGHTWARD))

        val result = EffectRegistry().onPlaceCard(RIGHT, effect, A5, defaultBoard).effectRegistry

        result should havePositionExtraPower(A4, 1, defaultBoard)
    }

    @Test
    fun `when a card power is lesser than negative extra power, should consider destroyable`() {
        val effect = RaisePower(-1, ANY, WhileActive, affected = setOf(RIGHTWARD))

        val result = EffectRegistry().onPlaceCard(LEFT, effect, A1, defaultBoard).effectRegistry

        result.getDestroyable(defaultBoard) should containAll(A2)
    }

    private fun boardWithFilledCells(vararg cells: Position): Board {
        val cell = Cell(LEFT, 1, SECURITY_OFFICER)
        return Board(state = cells.associateWith { cell })
    }
}
