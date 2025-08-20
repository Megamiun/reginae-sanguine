package br.com.gabryel.reginaesanguine.app.ui.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
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
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.app.ui.theme.createNumbersTextStyle
import br.com.gabryel.reginaesanguine.app.ui.util.getCardSize
import br.com.gabryel.reginaesanguine.app.util.NavigationScreens
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckViewModel

@Composable
context(_: CardImageLoader, _: PlayerContext, nav: NavigationManager<NavigationScreens>)
fun ViewDecks(deckViewModel: DeckViewModel) {
    val viewDecksState by deckViewModel.viewDecks.collectAsState()

    Column(Modifier, verticalArrangement = Arrangement.Center) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
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

        context(PlayerContext.left) {
            val deck = viewDecksState.selectedDeck
            Grid(IntSize(5, 3)) { position ->
                val card = deck.getOrNull(position.x * 5 + position.y)
                    ?: return@Grid

                DetailCard(card, getCardSize(60.dp))
            }
        }
    }
}
