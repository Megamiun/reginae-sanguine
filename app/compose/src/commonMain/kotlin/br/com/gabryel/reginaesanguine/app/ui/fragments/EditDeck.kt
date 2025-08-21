package br.com.gabryel.reginaesanguine.app.ui.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells.FixedSize
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gabryel.reginaesanguine.app.services.CardImageLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.app.ui.theme.YellowAccent
import br.com.gabryel.reginaesanguine.app.ui.theme.createTextStyle
import br.com.gabryel.reginaesanguine.app.ui.util.getCardSize
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.CardTier.COMMON
import br.com.gabryel.reginaesanguine.domain.CardTier.LEGENDARY
import br.com.gabryel.reginaesanguine.viewmodel.deck.DeckViewModel
import br.com.gabryel.reginaesanguine.viewmodel.deck.EditDeck

val backgroundColor = Color(16, 25, 25)

@Composable
context(_: CardImageLoader, _: PlayerContext)
fun EditDeck(deckViewModel: DeckViewModel) {
    val editDeckState by deckViewModel.editDeck.collectAsState()
    val viewDecksState by deckViewModel.viewDecks.collectAsState()

    val editDeck = editDeckState ?: return

    Surface(Modifier.fillMaxSize(), color = Black.copy(alpha = 0.7f)) {
        Column(
            Modifier.fillMaxSize().padding(vertical = 10.dp),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = spacedBy(4.dp),
        ) {
            val baseModifier = Modifier.fillMaxWidth(0.9f)

            Row(baseModifier, horizontalArrangement = Arrangement.Start) {
                Text(
                    "Deck #${viewDecksState.selectedDeckIndex + 1} (${editDeck.deck.size}/${deckViewModel.deckLimit})",
                    color = YellowAccent,
                )
            }

            Column(Modifier.height(100.dp).background(backgroundColor), verticalArrangement = Arrangement.SpaceBetween) {
                Spacer(Modifier.height(1.dp).fillMaxWidth().background(WhiteLight))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = CenterVertically,
                ) {
                    editDeck.deck.forEach { card ->
                        Column {
                            DetailCard(card, getCardSize(70.dp), Modifier.padding(2.dp, 5.dp))
                        }
                    }
                }
                Spacer(Modifier.height(1.dp).fillMaxWidth().background(WhiteLight))
            }

            Box(baseModifier) {
                Text("Cards Owned", Modifier.align(CenterStart), color = YellowAccent)
            }

            BoxWithConstraints(Modifier.weight(1f)) {
                val buttonsHeight = 30.dp
                val gridHeight = maxHeight - buttonsHeight
                val cardSize = getCardSize(80.dp)

                LazyVerticalGrid(
                    FixedSize(cardSize.width + 10.dp),
                    baseModifier.height(gridHeight).background(backgroundColor).border(1.dp, WhiteLight),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    items(deckViewModel.pack.cards.filterNot { it.spawnOnly }) { card ->
                        GridCell(card, editDeck, deckViewModel, cardSize)
                    }
                }

                CompositionLocalProvider(LocalTextStyle provides createTextStyle(0.5f)) {
                    Row(
                        baseModifier.height(buttonsHeight).align(BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Button({ deckViewModel.cancelEditMode() }) {
                            Text("RETURN")
                        }
                        Button({ deckViewModel.saveDeck() }) {
                            Text("SAVE")
                        }
                    }
                }
            }
        }
    }
}

@Composable
context(_: CardImageLoader, _: PlayerContext)
private fun GridCell(
    card: Card,
    editDeck: EditDeck,
    deckViewModel: DeckViewModel,
    cardSize: DpSize
) {
    val max = editDeck.getMax(card)
    val available = editDeck.getAvailable(card)

    val circleModifier = Modifier
        .size(12.dp)
        .clip(CircleShape)
        .background(Black)
        .border(1.dp, WhiteLight, CircleShape)
        .clickable(available > 0) { deckViewModel.addToDeck(card) }

    val shape = RoundedCornerShape(6.dp)
    val counterModifier = Modifier
        .clip(shape)
        .background(Black)
        .border(1.dp, WhiteLight, shape)
        .padding(8.dp, 1.dp)

    Column(Modifier.padding(5.dp, 7.dp)) {
        DetailCard(card, cardSize)
        CompositionLocalProvider(LocalTextStyle provides TextStyle(fontSize = 8.sp, color = YellowAccent)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = spacedBy(2.dp, CenterHorizontally),
                verticalAlignment = CenterVertically,
            ) {
                Text("$available/$max", counterModifier.align(CenterVertically), textAlign = TextAlign.Center)

                Box(circleModifier, contentAlignment = Center) {
                    Text("+")
                }
            }
        }
    }
}
