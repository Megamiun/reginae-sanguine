package br.com.gabryel.reginaesanguine.app.ui.fragments.deck

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gabryel.reginaesanguine.app.services.PainterLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.components.FancyBox
import br.com.gabryel.reginaesanguine.app.ui.components.Grid
import br.com.gabryel.reginaesanguine.app.ui.components.RButton
import br.com.gabryel.reginaesanguine.app.ui.decorations.addFancyCorners
import br.com.gabryel.reginaesanguine.app.ui.fragments.SimpleCard
import br.com.gabryel.reginaesanguine.app.ui.theme.FancyBoxBg
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.app.ui.theme.YellowAccent
import br.com.gabryel.reginaesanguine.app.ui.theme.createNumbersTextStyle
import br.com.gabryel.reginaesanguine.app.ui.util.getCardSize
import br.com.gabryel.reginaesanguine.viewmodel.deck.SingleDeckViewModel

@Composable
context(_: PainterLoader, player: PlayerContext)
fun ViewDecks(deckViewModel: SingleDeckViewModel) {
    val viewDecksState by deckViewModel.viewDecks.collectAsState()
    val cardSize = getCardSize(80.dp)

    val deck = viewDecksState.selectedDeck

    FancyBox {
        header(Modifier.background(player.color.copy(alpha = 0.8f)), FancyBoxBg) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp, 2.dp),
                SpaceBetween,
                CenterVertically,
            ) {
                Text("Deck #${viewDecksState.selectedDeckIndex + 1}", color = YellowAccent)
                RButton("Edit") { deckViewModel.enterEditMode() }
            }
        }

        body(Modifier.background(player.color.copy(alpha = 0.2f)).addFancyCorners()) {
            Grid(
                IntSize(5, 3),
                Modifier.padding(16.dp),
                horizontalArrangement = spacedBy(3.dp),
                verticalArrangement = spacedBy(7.dp),
            ) { (x, y) ->
                val card = deck.getOrNull(x + (y * 5)) ?: this.run {
                    Box(Modifier.size(cardSize))
                    return@Grid
                }

                SimpleCard(card, cardSize)
            }
        }

        decorate {
            Row(Modifier.align(TopCenter).offset(y = (-10).dp)) {
                val colors = ButtonColors(player.color, WhiteLight, YellowAccent, Black)

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
