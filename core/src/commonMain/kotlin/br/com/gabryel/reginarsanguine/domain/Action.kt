package br.com.gabryel.reginarsanguine.domain

sealed class Action {
    data object Skip : Action()
    data class Play(val row: Int, val column: Int, val card: Card) : Action()
}