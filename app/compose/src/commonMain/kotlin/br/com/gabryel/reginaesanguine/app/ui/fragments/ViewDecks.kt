package br.com.gabryel.reginaesanguine.app.ui.fragments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gabryel.reginaesanguine.app.services.PainterLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.NavigationManager
import br.com.gabryel.reginaesanguine.app.ui.components.Grid
import br.com.gabryel.reginaesanguine.app.ui.components.RButton
import br.com.gabryel.reginaesanguine.app.ui.theme.Emerald
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.app.ui.theme.YellowAccent
import br.com.gabryel.reginaesanguine.app.ui.theme.createNumbersTextStyle
import br.com.gabryel.reginaesanguine.app.ui.util.getCardSize
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckViewModel

@Composable
context(_: PainterLoader, _: PlayerContext, nav: NavigationManager<NavigationScreens>)
fun ViewDecks(deckViewModel: DeckViewModel) {
    val viewDecksState by deckViewModel.viewDecks.collectAsState()
    val cardSize = getCardSize(80.dp)

    Box(Modifier.fillMaxSize().padding(15.dp), contentAlignment = Center) {
        RButton("Return", Modifier.align(TopStart)) { nav.pop() }

        Box(Modifier.width(cardSize.width * 5.8f), contentAlignment = TopCenter) {
            Column(Modifier.padding(top = 10.dp)) {
                val deck = viewDecksState.selectedDeck
                Row(
                    Modifier.fillMaxWidth().background(Emerald).border(1.dp, WhiteLight).padding(24.dp, 0.dp),
                    SpaceBetween,
                    CenterVertically,
                ) {
                    Text("Deck #${viewDecksState.selectedDeckIndex + 1}", color = YellowAccent)
                    RButton("Edit") { deckViewModel.enterEditMode() }
                }

                val gridModifier = Modifier.fillMaxWidth()
                    .height(cardSize.height * 3.8f)
                    .background(backgroundColor)
                    .border(1.dp, WhiteLight)

                Grid(
                    IntSize(5, 3),
                    gridModifier,
                    horizontalArrangement = spacedBy(3.dp, CenterHorizontally),
                ) { position ->
                    val card = deck.getOrNull(position.x + position.y * 5) ?: run {
                        Box(Modifier.size(cardSize))
                        return@Grid
                    }

                    context(PlayerContext.left) {
                        DetailCard(card, cardSize)
                    }
                }
            }

            Row {
                val colors = ButtonColors(Emerald, WhiteLight, YellowAccent, Black)

                repeat(viewDecksState.deckAmount) { index ->
                    RButton(
                        "${index + 1}",
                        Modifier.size(20.dp),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, WhiteLight),
                        colors = colors,
                        enabled = viewDecksState.selectedDeckIndex != index,
                        contentPadding = PaddingValues(0.dp),
                        textStyle = createNumbersTextStyle(16.sp),
                        textOffset = 0.dp,
                    ) { deckViewModel.changeDeckView(index) }
                }
            }
        }
    }
}
