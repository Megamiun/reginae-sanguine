package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.ui.components.FancyBox
import br.com.gabryel.reginaesanguine.app.ui.components.NavigationManager
import br.com.gabryel.reginaesanguine.app.ui.components.RButton
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.app.util.Mode
import br.com.gabryel.reginaesanguine.app.util.Mode.LOCAL
import br.com.gabryel.reginaesanguine.app.util.Mode.REMOTE
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens.HOME

@Composable
context(nav: NavigationManager<NavigationScreens>)
fun ModeScreen(changeMode: (Mode) -> Unit) {
    Box(Modifier.fillMaxSize(), Center) {
        FancyBox {
            header {
                Text("Choose Game Mode", Modifier.padding(horizontal = 24.dp), color = WhiteLight)
            }

            body {
                Column(Modifier.padding(8.dp), spacedBy(8.dp, CenterVertically)) {
                    RButton("Local", Modifier.width(100.dp)) {
                        changeMode(LOCAL)
                        nav.push(HOME)
                    }
                    RButton("Remote", Modifier.width(100.dp)) {
                        changeMode(REMOTE)
                        nav.push(HOME)
                    }
                }
            }
        }
    }
}
