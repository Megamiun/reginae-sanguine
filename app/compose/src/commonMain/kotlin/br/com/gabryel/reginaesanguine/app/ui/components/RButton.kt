package br.com.gabryel.reginaesanguine.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.ui.theme.GoldLight
import br.com.gabryel.reginaesanguine.app.ui.theme.GoldLightDisabled
import br.com.gabryel.reginaesanguine.app.ui.theme.buttonStyle

@Composable
fun RButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(20.dp),
    colors: ButtonColors = ButtonColors(GoldLight, Black, GoldLightDisabled, Black),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp, 5.dp),
    interactionSource: MutableInteractionSource? = null,
    textStyle: TextStyle = buttonStyle(),
    textOffset: Dp = 2.dp,
    onClick: () -> Unit,
) {
    val source = remember { interactionSource ?: MutableInteractionSource() }

    Surface(
        modifier = modifier.clickable(source, null, enabled, onClick = onClick),
        shape = shape,
        color = if (enabled) colors.containerColor else colors.disabledContainerColor,
        border = border,
    ) {
        Box(modifier = Modifier.padding(contentPadding), contentAlignment = Center) {
            Text(
                text = text.uppercase(),
                modifier = Modifier.offset(y = textOffset),
                style = textStyle,
                color = if (enabled) colors.contentColor else colors.disabledContentColor,
            )
        }
    }
}
