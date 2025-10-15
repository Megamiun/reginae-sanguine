package br.com.gabryel.reginaesanguine.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize.Max
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.ui.theme.Emerald
import br.com.gabryel.reginaesanguine.app.ui.theme.FancyBoxBg
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight

@Composable
fun FancyBox(modifier: Modifier = Modifier, setup: FancyBoxScope.() -> Unit) {
    val scope = FancyBoxScope().also(setup)

    Box(Modifier.width(Max).then(modifier)) {
        Column {
            scope.header?.let { it() }
            (scope.body)()
        }

        (scope.decorations)()
    }
}

class FancyBoxScope {
    var header: (@Composable () -> Unit)? = null
    var body: @Composable () -> Unit = {}
    var decorations: @Composable BoxScope.() -> Unit = {}

    fun header(modifier: Modifier = Modifier, background: Color = Emerald, content: @Composable BoxScope.() -> Unit) {
        val baseModifier = Modifier
            .fillMaxWidth()
            .background(background)
            .border(1.dp, WhiteLight)

        header = {
            Box(baseModifier.then(modifier), Center, content = content)
        }
    }

    fun body(modifier: Modifier = Modifier, background: Color = FancyBoxBg, content: @Composable BoxScope.() -> Unit) {
        val baseModifier = Modifier
            .fillMaxWidth()
            .offset(y = (-1).dp)
            .background(background)
            .border(1.dp, WhiteLight)

        body = {
            Box(baseModifier.then(modifier), Center, content = content)
        }
    }

    fun decorate(content: @Composable BoxScope.() -> Unit) {
        decorations = content
    }
}
