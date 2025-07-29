package br.com.gabryel.reginaesanguine.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.Success
import br.com.gabryel.reginaesanguine.domain.column
import br.com.gabryel.reginaesanguine.domain.lane
import br.com.gabryel.reginaesanguine.services.ResourceLoader
import br.com.gabryel.reginaesanguine.ui.components.Grid
import br.com.gabryel.reginaesanguine.ui.theme.Emerald
import br.com.gabryel.reginaesanguine.ui.theme.PurpleDark
import br.com.gabryel.reginaesanguine.ui.theme.Ruby
import br.com.gabryel.reginaesanguine.ui.theme.WhiteDark
import br.com.gabryel.reginaesanguine.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.ui.theme.Yellow
import br.com.gabryel.reginaesanguine.ui.theme.defaultTextSize
import br.com.gabryel.reginaesanguine.ui.theme.defaultTextStyle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val playerCellInternalModifier = Modifier
    .fillMaxSize()
    .border(1.dp, Black)

@Composable
fun GameBoard(game: Game, resourceLoader: ResourceLoader) {
    val lateralSize = IntSize(1, game.height)
    val gridSize = IntSize(game.width, game.height)

    Column(
        modifier = Modifier
            .background(WhiteDark)
            .padding(4.dp),
    ) {
        Row(modifier = Modifier.border(1.dp, Black)) {
            Grid(lateralSize, cellModifier = Modifier::playerCell) { position ->
                PlayerLanePowerCell(game, position, LEFT)
            }
            Grid(
                gridSize,
                modifier = Modifier.border(0.1.dp, WhiteDark),
                cellModifier = Modifier::boardCell,
            ) { position ->
                GridPlayableCell(game, position, resourceLoader)
            }
            Grid(lateralSize, cellModifier = Modifier::playerCell) { position ->
                PlayerLanePowerCell(game, position, RIGHT)
            }
        }
    }
}

@Composable
private fun BoxScope.GridPlayableCell(
    game: Game,
    position: Position,
    resourceLoader: ResourceLoader
) {
    val cellContent = game.getCellAt(position)

    if (cellContent !is Success || cellContent.value.owner == null)
        return

    val cell = cellContent.value

    val card = cell.card
    val owner = cell.owner

    if (owner == null) return

    val color = when (cell.owner) {
        LEFT -> Emerald
        else -> Ruby
    }

    if (card == null) {
        RankGroup(cell.rank, 10f, color)
        return
    }

    val image = resourceLoader.loadCardImage("queens_blood", owner, card.id)

    if (image != null) {
        Box(modifier = Modifier.matchParentSize(), contentAlignment = Center) {
            Image(painter = image, contentDescription = "TODO", contentScale = Fit)
        }
    } else {
        ArtlessCard(color, card)
    }
}

@Composable
private fun BoxScope.ArtlessCard(color: Color, card: Card) {
    Box(
        modifier = Modifier.matchParentSize().padding(2.dp).background(color),
        contentAlignment = Center,
    ) {
        Box(Modifier.size(27.dp).align(TopStart), contentAlignment = Center) {
            RankGroup(card.rank, 8f, color, multiplier = 3.5f)
        }
        Box(Modifier.size(27.dp).align(TopEnd), contentAlignment = Center) {
            PowerIndicator(card.power, Black, multiplier = 0.6f)
        }
        ResizableText(card.name, modifier = Modifier.rotate(90f).align(Center))
    }
}

@Composable
private fun ResizableText(text: String, maxFontSize: TextUnit = defaultTextSize(), modifier: Modifier = Modifier) {
    var fontSize by remember { mutableStateOf(maxFontSize) }
//    Text(
//        text,
//        modifier = modifier,
//        fontSize = fontSize,
//        maxLines = 1,
//        onTextLayout = { layout -> if (layout.multiParagraph.didExceedMaxLines) fontSize *= .98F },
//    )
}

@Composable
private fun BoxScope.RankGroup(rank: Int, size: Float, color: Color, multiplier: Float = 1f) {
    val rankModifier = Modifier
        .size(size.dp)
        .clip(CircleShape)
        .background(color)
        .border(1.dp, WhiteLight, CircleShape)

    when (rank) {
        1 -> Rank(rankModifier)
        2 -> {
            Rank(rankModifier, BiasAlignment(-0.2f * multiplier, 0f * multiplier))
            Rank(rankModifier, BiasAlignment(0.2f * multiplier, 0f * multiplier))
        }
        3 -> {
            Rank(rankModifier, BiasAlignment(-0.2f * multiplier, 0.15f * multiplier))
            Rank(rankModifier, BiasAlignment(0.2f * multiplier, 0.15f * multiplier))
            Rank(rankModifier, BiasAlignment(0f * multiplier, -0.15f * multiplier))
        }
    }
}

@Composable
private fun BoxScope.Rank(modifier: Modifier, alignment: Alignment = Center) {
    Box(modifier = modifier.align(alignment = alignment))
}

@Composable
private fun PlayerLanePowerCell(game: Game, position: Position, player: PlayerPosition) {
    val playerPower = game.getLaneScore(position.lane())[player] ?: 0

    val color = when (player) {
        LEFT -> Emerald
        RIGHT -> Ruby
    }

    Box(modifier = playerCellInternalModifier, contentAlignment = Center) {
        PowerIndicator(playerPower, color)
    }
}

@Composable
private fun PowerIndicator(power: Int, color: Color, multiplier: Float = 1f) {
    val circleModifier = Modifier.size(35.dp * multiplier).clip(CircleShape).background(Yellow)
    val smallCircleModifier = Modifier.size(23.dp * multiplier).clip(CircleShape).background(color)

    Box(modifier = circleModifier, contentAlignment = Center) {
        listOf(30f, 150f, 270f).forEach {
            Box(modifier = smallCircleModifier.align(findAlignmentBias(it, 0.85f)))
        }

//        Text(power.toString(), style = defaultTextStyle(multiplier))
    }
}

private fun findAlignmentBias(angle: Float, distance: Float): Alignment {
    val angleRadians = (angle * PI / 180).toFloat()
    return BiasAlignment(cos(angleRadians) * distance, sin(angleRadians) * distance)
}

private fun Modifier.boardCell(position: Position): Modifier =
    if ((position.lane() + position.column()) % 2 == 0) {
        cellSize().background(WhiteLight)
    } else {
        cellSize().background(PurpleDark)
    }

private fun Modifier.playerCell(position: Position): Modifier =
    cellSize()
        .background(PurpleDark)
        .padding(1.25.dp)

private fun Modifier.cellSize(): Modifier =
    width(90.dp).aspectRatio(0.73f)
