package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.services.CardImageLoader
import br.com.gabryel.reginaesanguine.app.ui.components.ResizableText
import br.com.gabryel.reginaesanguine.app.ui.theme.Emerald
import br.com.gabryel.reginaesanguine.app.ui.theme.Ruby
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT

@Composable
context(cardImageLoader: CardImageLoader)
fun Card(playerPosition: PlayerPosition, card: Card, modifier: Modifier = Modifier) {
    val color = when (playerPosition) {
        LEFT -> Emerald
        else -> Ruby
    }

    val image = cardImageLoader.loadCardImage("queens_blood", playerPosition, card.id)

    if (image != null) {
        Box(modifier = modifier, contentAlignment = Center) {
            Image(image, contentDescription = "TODO", contentScale = Fit)
        }
    } else {
        ArtlessCard(color, card, modifier)
    }
}

@Composable
private fun ArtlessCard(color: Color, card: Card, modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(2.dp).background(color), contentAlignment = Center) {
        Box(Modifier.size(27.dp).align(TopStart), contentAlignment = Center) {
            RankGroup(card.rank, 8f, color, multiplier = 3.5f)
        }
        Box(Modifier.size(27.dp).align(TopEnd), contentAlignment = Center) {
            PowerIndicator(card.power, Black, multiplier = 0.6f)
        }
        ResizableText(card.name, modifier = Modifier.rotate(90f).align(Center))
    }
}
