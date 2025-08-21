package br.com.gabryel.reginaesanguine.app.ui.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gabryel.reginaesanguine.app.services.CardImageLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.NavigationManager
import br.com.gabryel.reginaesanguine.app.ui.components.Grid
import br.com.gabryel.reginaesanguine.app.ui.theme.Emerald
import br.com.gabryel.reginaesanguine.app.ui.theme.PurpleDark
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.app.ui.theme.YellowAccent
import br.com.gabryel.reginaesanguine.app.ui.theme.createNumbersTextStyle
import br.com.gabryel.reginaesanguine.app.ui.util.getCardSize
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckViewModel

@Composable
context(_: CardImageLoader, _: PlayerContext, nav: NavigationManager<NavigationScreens>)
fun ViewDecks(deckViewModel: DeckViewModel) {
    val viewDecksState by deckViewModel.viewDecks.collectAsState()
    val cardSize = getCardSize(80.dp)

    Box(Modifier.fillMaxSize().background(PurpleDark), contentAlignment = Center) {
        Button({ nav.pop() }, Modifier.align(TopStart)) {
            Text("RETURN")
        }

        Column(Modifier.width(cardSize.width * 5.8f), verticalArrangement = Arrangement.Center) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val circleModifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Black)
                    .border(1.dp, WhiteLight, CircleShape)

                Row {
                    repeat(viewDecksState.deckAmount) { index ->
                        val clickableModifier = circleModifier.clickable { deckViewModel.changeDeckView(index) }
                        Box(clickableModifier, contentAlignment = Center) {
                            Text(
                                "${index + 1}",
                                style = createNumbersTextStyle(12.sp),
                                color = WhiteLight,
                            )
                        }
                    }
                }

                Button({ deckViewModel.enterEditMode() }) {
                    Text("EDIT")
                }
            }

            val deck = viewDecksState.selectedDeck
            Row(
                Modifier.fillMaxWidth().background(Emerald).border(1.dp, WhiteLight),
                horizontalArrangement = Arrangement.Start,
            ) {
                Text(
                    "Deck #${viewDecksState.selectedDeckIndex + 1} (${deck.size}/${deckViewModel.deckLimit})",
                    color = YellowAccent,
                )
            }

            val gridModifier = Modifier.fillMaxWidth().height(cardSize.height * 3.8f).background(backgroundColor)
                .border(1.dp, WhiteLight)
            Grid(IntSize(5, 3), gridModifier, horizontalArrangement = spacedBy(3.dp, CenterHorizontally)) { position ->
                val card = deck.getOrNull(position.x + position.y * 5) ?: run {
                    Box(Modifier.size(cardSize))
                    return@Grid
                }

                context(PlayerContext.left) {
                    DetailCard(card, cardSize)
                }
            }
        }
    }
}
