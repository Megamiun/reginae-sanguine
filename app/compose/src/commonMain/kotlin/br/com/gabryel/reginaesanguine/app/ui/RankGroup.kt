package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.Res
import br.com.gabryel.reginaesanguine.app.services.PlayerContext
import br.com.gabryel.reginaesanguine.app.static_pawn
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import org.jetbrains.compose.resources.painterResource

@Composable
fun RowRankGroup(rank: Int, pinWidth: Dp, modifier: Modifier = Modifier) {
    val pawn = painterResource(Res.drawable.static_pawn)

    Row(modifier) {
        repeat(rank) {
            Image(pawn, null, Modifier.width(pinWidth).padding(1.dp))
        }
    }
}

@Composable
context(player: PlayerContext)
fun CellRankGroup(rank: Int, modifier: Modifier = Modifier, size: Dp) {
    val rankModifier = Modifier
        .size(size / 4)
        .clip(CircleShape)
        .background(player.color)
        .border(1.dp, WhiteLight, CircleShape)

    Box(modifier.size(size)) {
        when (rank) {
            1 -> Rank(rankModifier)
            2 -> {
                Rank(rankModifier, BiasAlignment(-0.5f, 0f))
                Rank(rankModifier, BiasAlignment(0.5f, 0f))
            }
            3 -> {
                Rank(rankModifier, BiasAlignment(-0.5f, 0.375f))
                Rank(rankModifier, BiasAlignment(0.5f, 0.375f))
                Rank(rankModifier, BiasAlignment(0f, -0.375f))
            }
        }
    }
}

@Composable
private fun BoxScope.Rank(modifier: Modifier, alignment: Alignment = Center) {
    Spacer(modifier.align(alignment = alignment))
}
