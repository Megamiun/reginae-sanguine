package br.com.gabryel.reginaesanguine.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import br.com.gabryel.reginaesanguine.app.services.ResCardImageLoader
import br.com.gabryel.reginaesanguine.app.services.ResourcesResourceLoader
import br.com.gabryel.reginaesanguine.app.ui.App
import br.com.gabryel.reginaesanguine.app.ui.GameBoard
import br.com.gabryel.reginaesanguine.app.ui.theme.ReginaeSanguineTheme
import br.com.gabryel.reginaesanguine.app.util.createTestDeck
import br.com.gabryel.reginaesanguine.domain.Board
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.atColumn
import br.com.gabryel.reginaesanguine.viewmodel.GameViewModel
import kotlin.system.exitProcess

fun main() = application {
    context(ResCardImageLoader()) {
        Window(title = "Reginae Sanguine", onCloseRequest = { exitProcess(0) }) {
            ReginaeSanguineTheme {
                Scaffold { paddingValues ->
                    Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Center) {
                        App(ResourcesResourceLoader())
                    }
                }
            }
        }
    }
}
