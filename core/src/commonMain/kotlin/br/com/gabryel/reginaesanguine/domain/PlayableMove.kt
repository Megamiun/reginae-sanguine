package br.com.gabryel.reginaesanguine.domain

import kotlinx.serialization.Serializable

@Serializable
data class PlayableMove(val position: Position, val cardId: String)
