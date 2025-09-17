package br.com.gabryel.reginaesanguine.viewmodel.deck

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckEditState.DeckEdit
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckEditState.DeckView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface DeckEditState {
    object DeckView : DeckEditState

    data class DeckEdit(val player: PlayerPosition, val editDeck: EditDeck, val playerViewModel: SingleDeckViewModel) : DeckEditState
}

sealed interface DeckEditViewModel {
    val leftPlayer: SingleDeckViewModel
    val editState: StateFlow<DeckEditState>
}

class RemoteDeckViewModel(override val leftPlayer: SingleDeckViewModel) : DeckEditViewModel {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override val editState: StateFlow<DeckEditState> = leftPlayer.editDeck.map { editDeck ->
        editDeck ?: return@map DeckView

        DeckEdit(LEFT, editDeck, leftPlayer)
    }.stateIn(scope, Eagerly, DeckView)
}

class LocalDeckViewModel(override val leftPlayer: SingleDeckViewModel, val rightPlayer: SingleDeckViewModel) : DeckEditViewModel {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override val editState: StateFlow<DeckEditState> = combine(leftPlayer.editDeck, rightPlayer.editDeck) { left, right ->
        when {
            left != null -> DeckEdit(LEFT, left, leftPlayer)
            right != null -> DeckEdit(RIGHT, right, rightPlayer)
            else -> DeckView
        }
    }.stateIn(scope, Eagerly, DeckView)
}
