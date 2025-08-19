package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens.GAME

@Composable
context(nav: NavigationManager<NavigationScreens>)
fun HomeScreen() {
    Button({ nav.push(GAME) }, Modifier.size(100.dp, 30.dp)) {
        Text("PLAY")
    }
}
