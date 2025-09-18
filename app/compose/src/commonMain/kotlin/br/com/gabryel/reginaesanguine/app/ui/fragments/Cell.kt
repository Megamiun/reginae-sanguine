package br.com.gabryel.reginaesanguine.app.ui.fragments

import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.services.PainterLoader
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.theme.YellowAccent
import br.com.gabryel.reginaesanguine.app.util.getContent
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.viewmodel.game.GamePlayerSummary

private val playerCellInternalModifier = Modifier.fillMaxSize().border(1.dp, Black)

@Composable
context(_: PainterLoader)
fun GridPlayableCell(
    game: GamePlayerSummary,
    position: Position,
    cardSize: DpSize,
    isPlayable: (String) -> Boolean,
    putCard: (String) -> Boolean
) {
    val cell = game.getCellAt(position)

    val owner = cell?.owner ?: return
    val card = cell.card

    context(PlayerContext.getDefaultFor(owner)) {
        Box(Modifier.dragAndDrop(isPlayable, putCard), contentAlignment = Center) {
            if (card == null) {
                Box(Modifier.size(cardSize), contentAlignment = Center) {
                    CellRankGroup(
                        cell.rank,
                        size = cardSize.width / 2,
                    )
                }
            } else {
                SimpleCard(card, cardSize)
            }
        }
    }
}

@Composable
private fun Modifier.dragAndDrop(isPlayable: (String) -> Boolean, putCard: (String) -> Boolean): Modifier {
    var isDroppable by remember { mutableStateOf(false) }

    // TODO Check about using app events instead of drag and drop events.
    //  Drag and Drop events are pretty workaroundy, as the clipdata is only available on DROP events.
    val dropCallback = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent) = getContent(event)?.let(putCard) ?: false

            override fun onStarted(event: DragAndDropEvent) {
                isDroppable = getContent(event)?.let(isPlayable) ?: false
            }

            override fun onEnded(event: DragAndDropEvent) {
                isDroppable = false
            }
        }
    }

    return dragAndDropTarget({ true }, dropCallback).drawBehind {
        if (!isDroppable)
            return@drawBehind

        repeat(3) { i ->
            drawRoundRect(
                color = YellowAccent.copy(alpha = 0.3f / (i + 1)),
                style = Stroke(width = (8 + i * 4).dp.toPx()),
                cornerRadius = CornerRadius(8.dp.toPx()),
            )
        }

        drawRoundRect(
            color = YellowAccent,
            style = Stroke(width = 4.dp.toPx()),
            cornerRadius = CornerRadius(8.dp.toPx()),
        )
    }
}

@Composable
context(player: PlayerContext)
fun PlayerLanePowerCell(game: GamePlayerSummary, position: Position) {
    val playerPower = game.getBaseLaneScoreAt(position.lane)[player.position] ?: 0

    Box(modifier = playerCellInternalModifier, contentAlignment = Center) {
        PlayerPowerIndicator(playerPower, 25.dp, accented = game.getLaneWinner(position.lane) == player.position)
    }
}
