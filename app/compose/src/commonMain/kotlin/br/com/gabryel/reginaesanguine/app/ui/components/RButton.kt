package br.com.gabryel.reginaesanguine.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.CombinedModifier
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
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp, 5.dp),
    interactionSource: MutableInteractionSource? = null,
    textStyle: TextStyle = buttonStyle(),
    textOffset: Dp = 2.dp,
    onClick: () -> Unit,
) {
    Button(
        onClick,
        CombinedModifier(Modifier.defaultMinSize(1.dp, 1.dp), modifier),
        enabled,
        shape,
        colors,
        elevation,
        border,
        contentPadding,
        interactionSource,
    ) {
        Text(text.uppercase(), Modifier.offset(y = textOffset).align(CenterVertically), style = textStyle)
    }
}
