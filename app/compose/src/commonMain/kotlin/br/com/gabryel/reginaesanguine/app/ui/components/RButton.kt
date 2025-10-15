package br.com.gabryel.reginaesanguine.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.ui.theme.Border
import br.com.gabryel.reginaesanguine.app.ui.theme.ButtonBackground
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.app.ui.theme.buttonStyle

private const val DISABLED_ALPHA = 0.3f

val DEFAULT_COLORS = ButtonColors(
    ButtonBackground,
    WhiteLight,
    ButtonBackground.copy(alpha = DISABLED_ALPHA),
    WhiteLight.copy(alpha = DISABLED_ALPHA),
)

@Composable
fun RButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(10.dp),
    colors: ButtonColors = DEFAULT_COLORS,
    border: BorderStroke = BorderStroke(1.dp, Border),
    disabledBorder: BorderStroke = BorderStroke(1.dp, Border.copy(alpha = DISABLED_ALPHA)),
    contentPadding: PaddingValues = PaddingValues(12.dp, 1.5.dp),
    interactionSource: MutableInteractionSource? = null,
    textStyle: TextStyle = buttonStyle(),
    textOffset: Dp = 2.dp,
    onClick: () -> Unit,
) {
    val source = remember { interactionSource ?: MutableInteractionSource() }

    val correctedBorder = if (enabled) border else disabledBorder
    val correctedBackgroundColor = if (enabled) colors.containerColor else colors.disabledContainerColor
    val correctedContentColor = if (enabled) colors.contentColor else colors.disabledContentColor

    Surface(
        modifier = modifier.defaultMinSize(minWidth = 80.dp).clickable(source, null, enabled, onClick = onClick),
        shape = shape,
        color = correctedBackgroundColor,
        border = correctedBorder,
    ) {
        Box(modifier = Modifier.padding(contentPadding), contentAlignment = Center) {
            Text(
                text = text.uppercase(),
                modifier = Modifier.offset(y = textOffset),
                style = textStyle,
                color = correctedContentColor,
            )
        }
    }
}
