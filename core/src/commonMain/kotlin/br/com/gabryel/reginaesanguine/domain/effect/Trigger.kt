package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.effect.TargetType.SELF
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface Trigger

@Serializable
@SerialName("WhenPlayed")
class WhenPlayed(val scope: TargetType = SELF) : Trigger

@Serializable
class WhenDestroyed(val scope: TargetType = SELF) : Trigger

@Serializable
class OnStatusChange(val status: StatusType = StatusType.ANY, val scope: TargetType = TargetType.ANY) : Trigger

@Serializable
class WhenFirstStatusChanged(val status: StatusType) : Trigger

@Serializable
class WhenFirstReachesPower(val threshold: Int) : Trigger

@Serializable
object WhenLaneWon : Trigger

@Serializable
object WhileActive : Trigger

@Serializable
object OnRoundEnd : Trigger

@Serializable
object None : Trigger
