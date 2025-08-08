package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.effect.TargetType.SELF
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface Trigger {
    val isPermanent: Boolean
}

@Serializable
@SerialName("WhenPlayed")
class WhenPlayed(val scope: TargetType = SELF) : Trigger {
    override val isPermanent = true
}

@Serializable
@SerialName("WhenDestroyed")
class WhenDestroyed(val scope: TargetType = SELF) : Trigger {
    override val isPermanent = true
}

@Serializable
@SerialName("OnStatusChange")
class OnStatusChange(val status: StatusType = StatusType.ANY, val scope: TargetType = TargetType.ANY) : Trigger {
    override val isPermanent = false
}

@Serializable
@SerialName("WhenFirstStatusChanged")
class WhenFirstStatusChanged(val status: StatusType) : Trigger {
    override val isPermanent = true
}

@Serializable
@SerialName("WhenFirstReachesPower")
class WhenFirstReachesPower(val threshold: Int) : Trigger {
    override val isPermanent = true
}

@Serializable
@SerialName("WhenLaneWon")
object WhenLaneWon : Trigger {
    override val isPermanent = false
}

@Serializable
@SerialName("WhileActive")
object WhileActive : Trigger {
    override val isPermanent = false
}

@Serializable
@SerialName("OnGameEnd")
object OnGameEnd : Trigger {
    override val isPermanent = false
}

@Serializable
@SerialName("None")
object None : Trigger {
    override val isPermanent = true
}
