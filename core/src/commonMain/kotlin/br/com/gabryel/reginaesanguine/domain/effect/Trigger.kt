package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.effect.TargetType.SELF

interface Trigger

class WhenPlayed(val scope: TargetType = SELF) : Trigger

class WhenDestroyed(val scope: TargetType = SELF) : Trigger

class OnStatusChange(val status: StatusType = StatusType.ANY, val scope: TargetType = TargetType.ANY) : Trigger

class WhenFirstStatusChanged(val status: StatusType) : Trigger

class WhenFirstReachesPower(val threshold: Int) : Trigger

object WhenLaneWon : Trigger

object WhileActive : Trigger

object OnRoundEnd : Trigger

object None : Trigger
