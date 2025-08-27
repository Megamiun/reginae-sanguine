package br.com.gabryel.reginaesanguine.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition.Companion.Right
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import br.com.gabryel.reginaesanguine.app.services.LocalInteractionType
import br.com.gabryel.reginaesanguine.app.util.InteractionType
import br.com.gabryel.reginaesanguine.app.util.InteractionType.MOUSE
import br.com.gabryel.reginaesanguine.app.util.InteractionType.TOUCH

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionableTooltip(
    actionTitle: String,
    action: () -> Unit,
    tooltip: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val interactionType = LocalInteractionType.current

    val actionButton = createActionButton(interactionType, actionTitle, action)

    TooltipBox(
        TooltipDefaults.rememberTooltipPositionProvider(Right),
        { RichTooltip(shape = RectangleShape, action = actionButton, text = tooltip) },
        rememberTooltipState(isPersistent = true),
        Modifier.clickable(interactionType == MOUSE, onClick = action),
        content = content,
    )
}

private fun createActionButton(interactionType: InteractionType, actionTitle: String, action: () -> Unit): @Composable (() -> Unit)? =
    when (interactionType) {
        TOUCH -> {
            { ActionButton(actionTitle, action) }
        }
        else -> null
    }

@Composable
private fun ActionButton(actionTitle: String, action: () -> Unit) {
    Button(action) {
        Text(actionTitle)
    }
}
