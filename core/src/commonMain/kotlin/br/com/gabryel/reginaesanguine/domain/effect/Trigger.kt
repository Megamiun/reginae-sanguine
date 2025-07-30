package br.com.gabryel.reginaesanguine.domain.effect

interface Trigger

class WhenPlayed(val scope: TargetType) : Trigger

class OnStatusChange(val status: StatusType, val scope: TargetType)

class WhenFirstStatusChange(val status: StatusType) : Trigger

class WhenFirstReachesPower(val threshold: Int) : Trigger

object WhenLaneWon : Trigger

object WhenDestroyed : Trigger

object WhileActive : Trigger

object OnRoundEnd : Trigger

object None : Trigger
