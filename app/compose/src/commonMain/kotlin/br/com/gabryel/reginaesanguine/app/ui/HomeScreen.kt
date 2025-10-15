package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.Res
import br.com.gabryel.reginaesanguine.app.services.PainterLoader
import br.com.gabryel.reginaesanguine.app.static_temp_boardgamegeek_logo
import br.com.gabryel.reginaesanguine.app.ui.components.NavigationManager
import br.com.gabryel.reginaesanguine.app.ui.components.RButton
import br.com.gabryel.reginaesanguine.app.util.Mode
import br.com.gabryel.reginaesanguine.app.util.Mode.LOCAL
import br.com.gabryel.reginaesanguine.app.util.Mode.REMOTE
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens.DECK_SELECTION
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens.GAME

@Composable
context(nav: NavigationManager<NavigationScreens>, painterLoader: PainterLoader, mode: Mode)
fun HomeScreen(changeMode: (Mode) -> Unit) {
    Box(Modifier.fillMaxSize().padding(25.dp)) {
        val logo = painterLoader.loadStaticImage(Res.drawable.static_temp_boardgamegeek_logo)

        Image(logo, null, Modifier.align(Center))

        Row(Modifier.align(TopStart), spacedBy(8.dp, CenterHorizontally)) {
            val inverse = if (mode == REMOTE) LOCAL else REMOTE

            RButton(inverse.name) { changeMode(inverse) }
        }

        Row(Modifier.align(BottomCenter), horizontalArrangement = spacedBy(4.dp)) {
            RButton("Deck") { nav.push(DECK_SELECTION) }
            RButton("Play") { nav.push(GAME) }
        }
    }
}
