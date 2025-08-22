package br.com.gabryel.reginaesanguine.app.ui.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipAnchorPosition.Companion.Right
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gabryel.reginaesanguine.app.services.CardImageLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.components.Grid
import br.com.gabryel.reginaesanguine.app.ui.theme.GoldLight
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.app.ui.theme.YellowAccent
import br.com.gabryel.reginaesanguine.app.ui.theme.createTextStyle
import br.com.gabryel.reginaesanguine.app.ui.util.getCardSize
import br.com.gabryel.reginaesanguine.app.ui.util.getCardSizeByWidth
import br.com.gabryel.reginaesanguine.domain.Card
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
        ) {
            val baseModifier = Modifier.fillMaxWidth(0.9f)

            Row(baseModifier, horizontalArrangement = Arrangement.Start) {
                Text(
                    "Deck #${viewDecksState.selectedDeckIndex + 1} (${editDeck.deck.size}/${deckViewModel.deckLimit})",
                    color = YellowAccent,
                )
            }

            Column(Modifier.padding(vertical = 10.dp).background(backgroundColor)) {
                Spacer(Modifier.height(1.dp).fillMaxWidth().background(WhiteLight))

                BoxWithConstraints(Modifier.padding(15.dp), contentAlignment = Center) {
                    val cardSize = getCardSizeByWidth(maxWidth / 16)
                    Grid(IntSize(15, 1), Modifier.fillMaxWidth(), horizontalArrangement = SpaceBetween) { position ->
                        Column {
                            val card = editDeck.deck.getOrNull(position.x) ?: run {
                                Box(Modifier.size(cardSize))
                                return@Column
                            }
                            DetailCard(card, cardSize)
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

                Row(
                    baseModifier.height(buttonsHeight).align(BottomCenter),
                    horizontalArrangement = SpaceBetween,
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
context(_: CardImageLoader, _: PlayerContext)
private fun GridCell(
    card: Card,
    editDeck: EditDeck,
    deckViewModel: DeckViewModel,
    cardSize: DpSize
) {
    val max = editDeck.getMax(card)
    val available = editDeck.getAvailable(card)

    val shape = RoundedCornerShape(6.dp)
    val counterModifier = Modifier
        .clip(shape)
        .background(Black)
        .border(1.dp, WhiteLight, shape)
        .padding(8.dp, 1.dp)

    TooltipBox(
        TooltipDefaults.rememberTooltipPositionProvider(Right),
        tooltip = {
            RichTooltip(shape = RectangleShape, action = { AddCardButton(deckViewModel, card) }) {
                CardDescription(card)
            }
        },
        state = rememberTooltipState(isPersistent = true),
    ) {
        Column(Modifier.padding(5.dp, 7.dp), horizontalAlignment = CenterHorizontally) {
            DetailCard(card, cardSize)
            Text("$available/$max", counterModifier, fontSize = 8.sp, lineHeight = 8.sp, color = YellowAccent, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun AddCardButton(deckViewModel: DeckViewModel, card: Card) {
    Button({ deckViewModel.addToDeck(card) }) {
        Text("Add Card")
    }
}

@Composable
private fun CardDescription(card: Card) {
    val shape = RoundedCornerShape(10.dp)
    val modifier = Modifier
        .fillMaxWidth()
        .clip(shape)
        .background(GoldLight)
        .border(1.dp, YellowAccent, shape)
        .padding(3.dp, 3.dp)

    CompositionLocalProvider(LocalTextStyle provides createTextStyle(color = YellowAccent)) {
        Column(verticalArrangement = spacedBy(6.dp)) {
            Row(modifier, horizontalArrangement = SpaceBetween) {
                Text("Card #${card.id}")
                Text(card.tier.name)
            }
            Text(card.name)
            Spacer(Modifier.height(1.dp).fillMaxWidth().background(GoldLight))

            Text(card.effect.description, lineHeight = LocalTextStyle.current.lineHeight * 1.5)
        }
    }
}
