package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight

@Composable
fun RankGroup(
    rank: Int,
    size: Float,
    bgColor: Color,
    modifier: Modifier = Modifier,
    multiplier: Float = 1f
) {
    val rankModifier = Modifier
        .size(size.dp)
        .clip(CircleShape)
        .background(bgColor)
        .border(1.dp, WhiteLight, CircleShape)

    Box(modifier = modifier) {
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
}

@Composable
private fun BoxScope.Rank(modifier: Modifier, alignment: Alignment = Center) {
    Box(modifier = modifier.align(alignment = alignment))
}
