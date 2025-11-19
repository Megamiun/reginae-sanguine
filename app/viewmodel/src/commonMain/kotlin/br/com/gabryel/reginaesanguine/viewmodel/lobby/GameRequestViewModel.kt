package br.com.gabryel.reginaesanguine.viewmodel.lobby

import br.com.gabryel.reginaesanguine.server.domain.GameRequestDto
import br.com.gabryel.reginaesanguine.server.domain.GameRequestStatusDto
import br.com.gabryel.reginaesanguine.server.domain.page.GameRequestPageDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface GameRequestState {
    data object Loading : GameRequestState

    data class GameRequestList(val lobbies: GameRequestPageDto) : GameRequestState

    data class WaitingForOpponent(val lobby: GameRequestDto) : GameRequestState

    data class GameStarted(val gameStarted: GameRequestStatusDto) : GameRequestState

    data class Error(val message: String) : GameRequestState
}

class GameRequestViewModel(
    private val gameRequestClient: GameRequestClient,
    private val coroutineScope: CoroutineScope,
) {
    private val stateFlow = MutableStateFlow<GameRequestState>(GameRequestState.Loading)
    val state = stateFlow.asStateFlow()

    private var pollingLobbyId: String? = null

    fun loadLobbies() {
        coroutineScope.launch {
            stateFlow.value = GameRequestState.Loading
            try {
                val lobbies = gameRequestClient.listAvailable()
                stateFlow.value = GameRequestState.GameRequestList(lobbies)
            } catch (e: Exception) {
                stateFlow.value = GameRequestState.Error(e.message ?: "Failed to load lobbies")
            }
        }
    }

    fun createLobby(deckStateId: String) {
        coroutineScope.launch {
            stateFlow.value = GameRequestState.Loading
            try {
                val lobby = gameRequestClient.create(deckStateId)
                stateFlow.value = GameRequestState.WaitingForOpponent(lobby)
                startPollingLobbyStatus(lobby.id)
            } catch (e: Exception) {
                stateFlow.value = GameRequestState.Error(e.message ?: "Failed to create lobby")
            }
        }
    }

    fun joinLobby(lobbyId: String, deckStateId: String) {
        coroutineScope.launch {
            stateFlow.value = GameRequestState.Loading
            try {
                val gameStarted = gameRequestClient.join(lobbyId, deckStateId)
                stateFlow.value = GameRequestState.GameStarted(gameStarted)
            } catch (e: Exception) {
                stateFlow.value = GameRequestState.Error(e.message ?: "Failed to join lobby")
            }
        }
    }

    private fun startPollingLobbyStatus(lobbyId: String) {
        pollingLobbyId = lobbyId
        coroutineScope.launch {
            while (pollingLobbyId == lobbyId) {
                delay(500)
                try {
                    val gameStarted = gameRequestClient.getStatus(lobbyId)
                    if (gameStarted != null) {
                        stateFlow.value = GameRequestState.GameStarted(gameStarted)
                        pollingLobbyId = null
                        break
                    }
                } catch (e: Exception) {
                    // Continue polling on error
                }
            }
        }
    }

    fun stopPolling() {
        pollingLobbyId = null
    }

    fun refreshLobbies() {
        if (state.value is GameRequestState.GameRequestList) {
            loadLobbies()
        }
    }
}
