package br.com.gabryel.reginarsanguine.domain

sealed interface Action {
    data object Skip : Action

    data class Play(val position: Position, val card: Card) : Action
}
