package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.ui.theme.Yellow
import br.com.gabryel.reginaesanguine.app.ui.theme.YellowAccent
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.viewmodel.GameViewModel

@Composable
fun ResultOverlay(gameViewModel: GameViewModel) {
    val state by gameViewModel.state.collectAsState()
    val game = state.game

    Surface(color = Black.copy(alpha = 0.3f), modifier = Modifier.fillMaxSize()) {
        ResultText(game)
    }
}

@Composable
private fun ResultText(game: Game) {
    val boxSize = DpSize(180.dp, 60.dp)

    BoxWithConstraints(Modifier.size(boxSize), contentAlignment = Center) {
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
}
