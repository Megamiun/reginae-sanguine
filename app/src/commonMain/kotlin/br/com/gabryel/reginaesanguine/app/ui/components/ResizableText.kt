package br.com.gabryel.reginaesanguine.app.ui.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.com.gabryel.reginaesanguine.app.ui.theme.createTextStyle

@Composable
fun ResizableText(text: String, modifier: Modifier = Modifier) {
    var multiplier by remember { mutableStateOf(1f) }
    Text(
        text,
        modifier = modifier,
        style = createTextStyle(multiplier),
        maxLines = 1,
        onTextLayout = { layout -> if (layout.multiParagraph.didExceedMaxLines) multiplier *= .97F },
    )
}
