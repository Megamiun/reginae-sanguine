package br.com.gabryel.reginarsanguine.domain

interface Playable<T: Playable<T>> {
    fun play(player: PlayerPosition, action: Action): T
}