package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.Res
import br.com.gabryel.reginaesanguine.app.services.PainterLoader
import br.com.gabryel.reginaesanguine.app.static_temp_boardgamegeek_logo
import br.com.gabryel.reginaesanguine.app.ui.components.RButton
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens.DECK_SELECTION
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens.GAME

@Composable
context(nav: NavigationManager<NavigationScreens>, painterLoader: PainterLoader)
fun HomeScreen() {
    Column(Modifier.fillMaxSize(), spacedBy(8.dp, Bottom), CenterHorizontally) {
        val logo = painterLoader.loadStaticImage(Res.drawable.static_temp_boardgamegeek_logo)

        Image(logo, null)

        Row(Modifier.padding(24.dp), horizontalArrangement = spacedBy(4.dp)) {
            RButton("Deck") { nav.push(DECK_SELECTION) }
            RButton("Play") { nav.push(GAME) }
        }
    }
}
