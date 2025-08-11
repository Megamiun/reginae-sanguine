package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.SELF
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Trigger

interface Scoped {
    val scope: TargetType

    fun isInScope(source: PlayerPosition, target: PlayerPosition, self: Boolean) =
        scope.isTargetable(source, target, self)
}

@Serializable
@SerialName("WhenPlayed")
class WhenPlayed(override val scope: TargetType = SELF) : Trigger, Scoped

@Serializable
@SerialName("WhenDestroyed")
class WhenDestroyed(override val scope: TargetType = SELF) : Trigger, Scoped

@Serializable
@SerialName("WhenFirstStatusChanged")
class WhenFirstStatusChanged(val status: StatusType) : Trigger

@Serializable
@SerialName("WhenFirstReachesPower")
class WhenFirstReachesPower(val threshold: Int) : Trigger

@Serializable
@SerialName("WhenLaneWon")
object WhenLaneWon : Trigger

@Serializable
@SerialName("WhileActive")
object WhileActive : Trigger

@Serializable
@SerialName("None")
object None : Trigger
