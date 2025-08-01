package br.com.gabryel.reginaesanguine.viewmodel

import br.com.gabryel.reginaesanguine.domain.Action
import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.Success

interface Playable {
    fun play(position: Position, cardId: String): State
}

sealed interface State {
    val game: Game
    val error: String?

    data class ChooseAction(override val game: Game, override val error: String? = null) : State, Playable {
        fun toChooseCard() = ChooseCard(game)

        override fun play(position: Position, cardId: String): State =
            when (val game = game.play(game.playerTurn, Play(position, cardId))) {
                is Success<Game> -> ChooseAction(game.value)
                is Failure -> copy(error = game.toString())
            }

        fun skip() = when (val game = game.play(game.playerTurn, Action.Skip)) {
            is Success<Game> -> ChooseAction(game.value)
            is Failure -> copy(error = game.toString())
        }
    }

    data class ChooseCard(override val game: Game, override val error: String? = null) : State, Playable {
        fun chooseCard(cardId: String) = ChoosePosition(game, cardId)

        override fun play(position: Position, cardId: String): State =
            when (val game = this.game.play(game.playerTurn, Play(position, cardId))) {
                is Success<Game> -> ChooseAction(game.value)
                is Failure -> copy(error = game.toString())
            }
    }

    data class ChoosePosition(override val game: Game, val cardId: String, override val error: String? = null) : State {
        fun play(position: Position) =
            when (val game = game.play(game.playerTurn, Play(position, cardId))) {
                is Success<Game> -> ChooseAction(game.value)
                is Failure -> copy(error = game.toString())
            }
    }
}
