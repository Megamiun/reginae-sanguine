package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.effect.TargetType.SELF
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface Trigger

@Serializable
@SerialName("WhenPlayed")
class WhenPlayed(val scope: TargetType = SELF) : Trigger

@Serializable
@SerialName("WhenDestroyed")
class WhenDestroyed(val scope: TargetType = SELF) : Trigger

@Serializable
@SerialName("OnStatusChange")
class OnStatusChange(val status: StatusType = StatusType.ANY, val scope: TargetType = TargetType.ANY) : Trigger

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
@SerialName("OnGameEnd")
object OnGameEnd : Trigger

@Serializable
@SerialName("None")
object None : Trigger
