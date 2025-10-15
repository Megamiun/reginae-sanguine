package br.com.gabryel.reginaesanguine.app.ui.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.ui.theme.Border
import br.com.gabryel.reginaesanguine.app.ui.theme.YellowAccent
import br.com.gabryel.reginaesanguine.app.ui.theme.createTextStyle
import br.com.gabryel.reginaesanguine.domain.Card

@Composable
fun CardDescription(card: Card) {
    val shape = RoundedCornerShape(10.dp)
    val modifier = Modifier
        .fillMaxWidth()
        .clip(shape)
        .background(Border)
        .border(1.dp, YellowAccent, shape)
        .padding(3.dp, 3.dp)

    Column(verticalArrangement = spacedBy(6.dp)) {
        CompositionLocalProvider(LocalTextStyle provides createTextStyle(color = Black)) {
            Row(modifier, horizontalArrangement = SpaceBetween, verticalAlignment = CenterVertically) {
                Text("Card #${card.id}")
                Text(card.tier.name)
            }
        }

        CompositionLocalProvider(LocalTextStyle provides createTextStyle(color = YellowAccent)) {
            Text(card.name)
            Spacer(Modifier.height(1.dp).fillMaxWidth().background(Border))

            Text(card.effect.description, lineHeight = LocalTextStyle.current.lineHeight * 1.5)
        }
    }
}
