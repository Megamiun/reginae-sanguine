package br.com.gabryel.reginaesanguine.app.ui.fragments

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.ui.theme.Yellow
import br.com.gabryel.reginaesanguine.app.ui.theme.YellowAccent
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.viewmodel.game.GamePlayerSummary

@Composable
fun ResultOverlay(game: GamePlayerSummary, boardSize: DpSize) {
    Surface(color = Black.copy(alpha = 0.5f), modifier = Modifier.fillMaxSize()) {
        val score = game.getScores()
        Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.Center) {
            with(PlayerContext.left) {
                Box(Modifier.height(boardSize.height), Center) {
                    PlayerPowerIndicator(
                        score[LEFT] ?: error("No score found for player LEFT"),
                        40.dp,
                        Modifier.padding(5.dp),
                    )
                }
            }
            Box(Modifier.size(boardSize), Center) {
                ResultText(game)
            }
            with(PlayerContext.right) {
                Box(Modifier.height(boardSize.height), Center) {
                    PlayerPowerIndicator(
                        score[RIGHT] ?: error("No score found for player RIGHT"),
                        40.dp,
                        Modifier.padding(5.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultText(game: GamePlayerSummary) {
    val boxSize = DpSize(180.dp, 60.dp)

    Canvas(Modifier.size(boxSize)) {
        drawArc(YellowAccent, 300f, 120f, false, size = size, style = Stroke(6f))
        drawArc(YellowAccent, 120f, 120f, false, size = size, style = Stroke(6f))
    }

    Box(
        Modifier
            .size(35.dp, 35.dp)
            .scale(4f, 1f)
            .background(radialGradient(colors = listOf(Yellow, Transparent))),
    )

    when (game.getWinner()) {
        LEFT -> Text("Victory", color = YellowAccent)
        RIGHT -> Text("Lost", color = YellowAccent)
        else -> Text("Tie", color = YellowAccent)
    }
}
