package br.com.gabryel.reginarsanguine.domain

sealed class Action {
    data object Skip : Action()

    data class Play(val position: Position, val card: Card) : Action()
}
