package br.com.gabryel.reginaesanguine.app.ui.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gabryel.reginaesanguine.app.services.CardImageLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.app.ui.theme.createNumbersTextStyle
import br.com.gabryel.reginaesanguine.app.ui.util.getCardSize
import br.com.gabryel.reginaesanguine.domain.CardTier.COMMON
import br.com.gabryel.reginaesanguine.domain.CardTier.LEGENDARY
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckViewModel

@Composable
context(_: CardImageLoader, _: PlayerContext)
fun EditDeck(deckViewModel: DeckViewModel) {
    val editDeckState by deckViewModel.editDeck.collectAsState()
    val viewDecksState by deckViewModel.viewDecks.collectAsState()

    val editDeck = editDeckState ?: return

    val circleModifier = Modifier
        .size(20.dp)
        .clip(CircleShape)
        .background(Black)
        .border(1.dp, WhiteLight, CircleShape)

    Column {
        Row {
            Text("Deck #${viewDecksState.selectedDeckIndex + 1}")
            Spacer(Modifier.width(2.dp))
            Text("(${editDeck.deck.size}/${editDeck.deckLimit})")
        }
        Row(horizontalArrangement = Arrangement.Center) {
            editDeck.deck.forEach { card ->
                DetailCard(card, getCardSize(80.dp))
            }
        }
        FlowRow(Modifier.verticalScroll(rememberScrollState()), maxItemsInEachRow = 12) {
            deckViewModel.pack.cards.filterNot { it.spawnOnly }.forEach { card ->
                val max = when (card.tier) {
                    COMMON -> 2
                    LEGENDARY -> 1
                }

                val available = max - editDeck.deck.filter { it.id == card.id }.size

                Column {
                    DetailCard(card, getCardSize(80.dp))
                    Row {
                        Text("($available/$max)")

                        val clickableModifier = circleModifier.clickable(available != 0) { deckViewModel.addToDeck(card) }
                        Box(clickableModifier, contentAlignment = Center) {
                            Text(
                                "+",
                                style = createNumbersTextStyle(12.sp),
                                color = WhiteLight,
                            )
                        }
                    }
                }
            }
        }
    }
}
