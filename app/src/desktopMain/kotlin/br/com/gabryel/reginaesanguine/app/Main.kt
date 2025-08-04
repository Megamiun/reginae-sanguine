package br.com.gabryel.reginaesanguine.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.darkColors
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import br.com.gabryel.reginaesanguine.app.services.ResCardImageLoader
import br.com.gabryel.reginaesanguine.app.ui.GameBoard
import kotlin.system.exitProcess

fun main() = application {
    val knownCardJpg = Card("monalisa", "jpg", "Mona Lisa")
    val knownCardPng = Card("card001", "png", "Security")
    val unknownCard = Card("custom", "png", "Custom")

    val useCompose = true
    val useCoil = !useCompose

    val allowsDragAndDrop = true

    val cards =
        listOf(knownCardPng, unknownCard) // Does not show knownCard2
//        listOf(knownCardJpg, unknownCard) // Shows all
//        listOf(knownCardJpg, knownCardPng, unknownCard) // Shows all

    context(ResCardImageLoader(useCoil)) {
        Window(title = "Reginae Sanguine", onCloseRequest = { exitProcess(0) }) {
            MaterialTheme(colors = darkColors(background = Color(63, 48, 60))) {
                Scaffold { paddingValues ->
                    Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Center) {
                        GameBoard(allowsDragAndDrop, cards)
                    }
                }
            }
        }
    }
}
