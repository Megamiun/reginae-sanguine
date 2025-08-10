import br.com.gabryel.reginaesanguine.domain.*
import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.RIGHTWARD
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.effect.StatusBonus
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ALLIES
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.SECURITY_OFFICER
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.cardOf
import br.com.gabryel.reginaesanguine.domain.util.buildResult

fun main() {
    val statusBonusCard = cardOf(
        "Status Bonus",
        increments = setOf(RIGHTWARD),
        effect = StatusBonus(3, -1, ALLIES, affected = setOf(RIGHTWARD))
    )

    val board = buildResult {
        Board.default()
            .play(LEFT, Play(1 to 2, statusBonusCard)).orRaiseError()
            .play(LEFT, Play(1 to 3, SECURITY_OFFICER)).orRaiseError()
    }

    when (board) {
        is Success -> {
            println("Board created successfully")
            val totalPower = board.value.getTotalPowerAt(1 to 3)
            println("Total power at (1,3): $totalPower")
            
            val cell = board.value.getCellAt(1 to 3).orNull()
            println("Cell at (1,3): $cell")
        }
        is Failure -> {
            println("Board creation failed: $board")
        }
    }
}