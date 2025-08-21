package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens.DECK_SELECTION
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens.GAME

@Composable
context(nav: NavigationManager<NavigationScreens>)
fun HomeScreen() {
    Box(contentAlignment = Center) {
        Row {
            Button({ nav.push(DECK_SELECTION) }, Modifier.size(100.dp, 30.dp)) {
                Text("DECK")
            }
            Button({ nav.push(GAME) }, Modifier.size(100.dp, 30.dp)) {
                Text("PLAY")
            }
        }
    }
}
