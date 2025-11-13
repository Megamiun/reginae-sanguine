package br.com.gabryel.reginaesanguine.app.ui.fragments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale.Companion.FillWidth
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import br.com.gabryel.reginaesanguine.app.Res
import br.com.gabryel.reginaesanguine.app.services.PainterLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.static_bg_blue
import br.com.gabryel.reginaesanguine.app.static_bg_red
import br.com.gabryel.reginaesanguine.app.ui.components.Grid
import br.com.gabryel.reginaesanguine.app.ui.theme.CardBorderLegendaryDark
import br.com.gabryel.reginaesanguine.app.ui.theme.CardBorderLegendaryLight
import br.com.gabryel.reginaesanguine.app.ui.theme.CardBorderStandardDark
import br.com.gabryel.reginaesanguine.app.ui.theme.CardBorderStandardLight
import br.com.gabryel.reginaesanguine.app.ui.theme.EffectGridBg
import br.com.gabryel.reginaesanguine.app.ui.theme.EffectGridBorder
import br.com.gabryel.reginaesanguine.app.ui.theme.EffectGridEffect
import br.com.gabryel.reginaesanguine.app.ui.theme.EffectGridEmpty
import br.com.gabryel.reginaesanguine.app.ui.theme.EffectGridIncrement
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.app.ui.theme.Yellow
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.CardTier.LEGENDARY
import br.com.gabryel.reginaesanguine.domain.CardTier.STANDARD
import br.com.gabryel.reginaesanguine.domain.Displacement
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.effect.type.EffectWithAffected

private val STANDARD_BORDER_COLORS = listOf(CardBorderStandardDark, CardBorderStandardLight, CardBorderStandardDark)
private val LEGENDARY_BORDER_COLORS = listOf(CardBorderLegendaryDark, CardBorderLegendaryLight, CardBorderLegendaryDark)

private val CARD_SHAPE = RoundedCornerShape(5)

@Composable
context(painterLoader: PainterLoader, player: PlayerContext)
fun SimpleCard(card: Card, size: DpSize, modifier: Modifier = Modifier) {
    Card(player.position, card, size, modifier, false)
}

@Composable
context(painterLoader: PainterLoader, player: PlayerContext)
fun DetailCard(card: Card, size: DpSize, modifier: Modifier = Modifier) {
    Card(player.position, card, size, modifier, true)
}

@Composable
context(painterLoader: PainterLoader)
private fun Card(owner: PlayerPosition, card: Card, size: DpSize, modifier: Modifier, detailed: Boolean) {
    val background = loadBackgroundForPlayer(owner)
    val cardArt = painterLoader.loadCardImage("queens_blood", card.id)

    val borderBrush = remember(card.tier) { verticalGradient(card.getCardBorderColors()) }
    val borderStroke = remember(card.tier) { BorderStroke(1.dp, borderBrush) }

    val totalWidth = size.width
    val powerIndicationSize = totalWidth / 4.5f

    val clippingModifier = Modifier.clip(CARD_SHAPE)

    Box(modifier) {
        Box(clippingModifier.requiredSize(size).background(borderBrush, CARD_SHAPE).padding(1.5.dp), Center) {
            Image(background, null, clippingModifier.fillMaxSize(), TopCenter, FillWidth)
            Box(clippingModifier.fillMaxSize().padding(1.dp).border(borderStroke, CARD_SHAPE), Center) {
                if (cardArt != null)
                    Image(cardArt, "TODO", clippingModifier.fillMaxSize(), TopCenter, FillWidth)

                if (detailed)
                    CardDetails(owner, card, totalWidth, size)
            }

            CardPowerIndicator(card.power, powerIndicationSize, Modifier.align(TopEnd))
        }
    }
}

@Composable
fun BoxScope.CardDetails(owner: PlayerPosition, card: Card, totalWidth: Dp, size: DpSize) {
    val notchHeight = size.height / 9
    val centerWidth = notchHeight / 10

    val notchBrush = remember(card.tier) { horizontalGradient(card.getCardBorderColors()) }

    Column(Modifier.align(BottomCenter), verticalArrangement = spacedBy((-notchHeight * 0.9f))) {
        Box(Modifier.fillMaxWidth().zIndex(1f), Center) {
            DisplacementGrid(owner, card, totalWidth / 3f)
        }

        NotchedBox(notchBrush, notchHeight, centerWidth) {
            val fontSize = defineFontSize(totalWidth, card)
            Text(
                card.name,
                Modifier.fillMaxWidth().padding(top = 1.dp, bottom = 2.dp),
                color = Yellow,
                fontSize = fontSize,
                lineHeight = fontSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
    val pinSize = size.width / 11
    RowRankGroup(card.rank, pinSize, Modifier.align(TopStart).offset(3.dp, 3.dp))
}

private fun Card.getCardBorderColors(): List<Color> = when (tier) {
    STANDARD -> STANDARD_BORDER_COLORS
    LEGENDARY -> LEGENDARY_BORDER_COLORS
}

@Composable
private fun DisplacementGrid(owner: PlayerPosition, card: Card, totalWidth: Dp, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(10)
    val modifier = modifier
        .border(0.5.dp, EffectGridBorder, shape)
        .background(SolidColor(EffectGridBg), shape, 0.9f)
        .padding(1.5.dp)

    Box(modifier) {
        Grid(IntSize(5, 5)) { position ->
            Spacer(Modifier.gridCell(position, owner, card, totalWidth / 6f))
        }
    }
}

private fun Modifier.gridCell(position: Position, owner: PlayerPosition, card: Card, width: Dp): Modifier {
    val displacement = owner.correct(position.asDisplacement() + Displacement(-2, -2))

    return size(width)
        .padding(0.4.dp)
        .setCellBackground(displacement, card)
        .setCellBorder(displacement, card)
}

private fun Modifier.setCellBackground(displacement: Displacement, card: Card): Modifier =
    when (displacement) {
        Displacement(0, 0) -> background(WhiteLight)
        in card.increments -> background(EffectGridIncrement)
        else -> background(SolidColor(EffectGridEmpty), alpha = 0.6f)
    }

private fun Modifier.setCellBorder(displacement: Displacement, card: Card): Modifier =
    if (displacement in ((card.effect as? EffectWithAffected)?.affected ?: emptySet()))
        border(0.4.dp, EffectGridEffect)
    else
        this

@Composable
context(painterLoader: PainterLoader)
private fun loadBackgroundForPlayer(owner: PlayerPosition): Painter {
    val backgroundId = when (owner) {
        LEFT -> Res.drawable.static_bg_blue
        RIGHT -> Res.drawable.static_bg_red
    }

    return painterLoader.loadStaticImage(backgroundId)
}

@Composable
private fun defineFontSize(totalWidth: Dp, card: Card): TextUnit =
    LocalDensity.current.run {
        val divisor = maxOf(12, (card.name.length - 4))
        (totalWidth / divisor).toSp()
    }
