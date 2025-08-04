package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.Card
import br.com.gabryel.reginaesanguine.app.services.CardImageLoader

@Composable
context(cardImageLoader: CardImageLoader)
fun BoxScope.GameBoard(allowsDragAndDrop: Boolean, cards: List<Card>) {
    Row(Modifier.align(BottomCenter).height(150.dp), horizontalArrangement = Center) {
        cards.forEach { card ->
            Card(
                card,
                Modifier.fillMaxHeight()
                    .aspectRatio(0.73f)
                    .let { if (allowsDragAndDrop) it.dragAndDropSource { offset -> null } else it },
            )
        }
    }
}

@Composable
context(cardImageLoader: CardImageLoader)
fun Card(card: Card, modifier: Modifier = Modifier) {
    val image = cardImageLoader.loadCardImage(card)

    if (image != null) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Image(image, contentDescription = "TODO", contentScale = Fit)
        }
    } else {
        Box(modifier.padding(2.dp).background(Black), contentAlignment = Alignment.Center) {
            Text(card.name, modifier = Modifier.rotate(90f).align(Alignment.Center))
        }
    }
}
