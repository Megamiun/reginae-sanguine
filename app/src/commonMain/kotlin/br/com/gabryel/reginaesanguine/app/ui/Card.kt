package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale.Companion.FillWidth
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import br.com.gabryel.app.generated.resources.Res
import br.com.gabryel.app.generated.resources.queens_blood_base_bg_blue
import br.com.gabryel.app.generated.resources.queens_blood_base_bg_red
import br.com.gabryel.reginaesanguine.app.services.CardImageLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.components.Grid
import br.com.gabryel.reginaesanguine.app.ui.theme.BrownDark
import br.com.gabryel.reginaesanguine.app.ui.theme.BrownLight
import br.com.gabryel.reginaesanguine.app.ui.theme.GreyDark
import br.com.gabryel.reginaesanguine.app.ui.theme.GreyLight
import br.com.gabryel.reginaesanguine.app.ui.theme.Orange
import br.com.gabryel.reginaesanguine.app.ui.theme.RubyAccent
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.app.ui.theme.Yellow
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Displacement
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.effect.type.EffectWithAffected
import org.jetbrains.compose.resources.painterResource

@Composable
context(cardImageLoader: CardImageLoader)
fun GridCard(playerPosition: PlayerPosition, card: Card, size: DpSize, modifier: Modifier = Modifier) {
    Card(playerPosition, card, size, modifier, false)
}

@Composable
context(cardImageLoader: CardImageLoader, player: PlayerContext)
fun DetailCard(card: Card, size: DpSize, modifier: Modifier = Modifier) {
    Card(player.position, card, size, modifier, true)
}

@Composable
context(cardImageLoader: CardImageLoader)
private fun Card(owner: PlayerPosition, card: Card, size: DpSize, modifier: Modifier, detailed: Boolean) {
    val background = when (owner) {
        LEFT -> painterResource(Res.drawable.queens_blood_base_bg_blue)
        RIGHT -> painterResource(Res.drawable.queens_blood_base_bg_red)
    }

    val cardArt = cardImageLoader.loadCardImage("queens_blood", card.id)

    val borderBrush = verticalGradient(listOf(GreyDark, GreyLight, GreyDark))
    val borderStroke = BorderStroke(1.5.dp, borderBrush)
    val cardShape = RoundedCornerShape(5)
    val totalWidth = size.width

    Box(modifier.size(size).background(borderBrush, cardShape), Center) {
        Box(Modifier.padding(2.5.dp).clip(cardShape), Center) {
            Image(background, null, Modifier.fillMaxSize(), TopCenter, FillWidth)
            Box(Modifier.fillMaxSize().padding(1.dp).border(borderStroke, cardShape), Center) {
                if (cardArt != null)
                    Image(cardArt, "TODO", Modifier.fillMaxSize(), TopCenter, FillWidth)

                if (detailed) {
                    Column(Modifier.align(BottomCenter), verticalArrangement = Arrangement.Center) {
                        Box(Modifier.fillMaxWidth(), Center) {
                            DisplacementGrid(owner, card)
                        }

                        Text(
                            card.name,
                            Modifier.fillMaxWidth().background(Black).padding(3.dp),
                            Yellow,
                            defineFontSize(totalWidth, card),
                            textAlign = TextAlign.Center,
                        )
                    }
                    val pinSize = size.width / 11
                    RowRankGroup(card.rank, pinSize, Modifier.align(TopStart).offset(3.dp, 3.dp))
                }
            }

            CardPowerIndicator(card.power, totalWidth / 4.5f, Modifier.align(TopEnd))
        }
    }
}

@Composable
private fun DisplacementGrid(owner: PlayerPosition, card: Card, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(10)
    val modifier = modifier
        .border(0.5.dp, GreyDark, shape)
        .background(SolidColor(BrownDark), shape, 0.9f)
        .padding(1.5.dp)

    Box(modifier) {
        Grid(IntSize(5, 5)) { position ->
            Spacer(Modifier.gridCell(position, owner, card))
        }
    }
}

private fun Modifier.gridCell(position: Position, owner: PlayerPosition, card: Card): Modifier {
    val displacement = owner.correct(position.asDisplacement() + Displacement(-2, -2))

    return size(3.5.dp)
        .padding(0.4.dp)
        .setCellBackground(displacement, card)
        .setCellBorder(displacement, card)
}

private fun Modifier.setCellBackground(displacement: Displacement, card: Card): Modifier =
    when (displacement) {
        Displacement(0, 0) -> background(WhiteLight)
        in card.increments -> background(Orange)
        else -> background(SolidColor(BrownLight), alpha = 0.6f)
    }

private fun Modifier.setCellBorder(displacement: Displacement, card: Card): Modifier =
    if (displacement in ((card.effect as? EffectWithAffected)?.affected ?: emptySet()))
        border(0.4.dp, RubyAccent)
    else
        this

@Composable
private fun defineFontSize(totalWidth: Dp, card: Card): TextUnit =
    LocalDensity.current.run {
        val divisor = maxOf(12, (card.name.length - 4))
        (totalWidth / divisor).toSp()
    }
