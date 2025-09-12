package br.com.gabryel.reginaesanguine.app.ui.fragments

import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runComposeUiTest
import br.com.gabryel.reginaesanguine.app.services.LocalInteractionType
import br.com.gabryel.reginaesanguine.app.ui.components.ActionableTooltip
import br.com.gabryel.reginaesanguine.app.util.InteractionType
import br.com.gabryel.reginaesanguine.app.util.InteractionType.MOUSE
import br.com.gabryel.reginaesanguine.app.util.InteractionType.TOUCH
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Test

@OptIn(ExperimentalTestApi::class)
class ActionableTooltipTest {
    @Test
    fun `given Interactiontype is MOUSE, when element not interacted, should only show main content`() = runComposeUiTest {
        createComponent(MOUSE)

        onNodeWithText("Content").assertExists()

        onNodeWithText("Tooltip").assertDoesNotExist()
        onNodeWithText("Act").assertDoesNotExist()
    }

    @Test
    fun `given Interactiontype is TOUCH, when element not interacted, should only show main content`() = runComposeUiTest {
        createComponent(TOUCH)

        onNodeWithText("Content").assertExists()

        onNodeWithText("Tooltip").assertDoesNotExist()
        onNodeWithText("Act").assertDoesNotExist()
    }

    @Test
    fun `given Interactiontype is MOUSE, when element hovered, should show tooltip with no call to action`() = runComposeUiTest {
        createComponent(MOUSE)

        onNodeWithText("Content").performMouseInput { moveTo(Offset(0f, 0f)) }

        onNodeWithText("Act").assertDoesNotExist()
        onNodeWithText("Tooltip").assertExists()
    }

    @Test
    fun `given Interactiontype is MOUSE, when element clicked, should execute action`() = runComposeUiTest {
        val mock = spyk<() -> Unit>()
        createComponent(MOUSE, mock)

        onNodeWithText("Content").performClick()

        verify(exactly = 1) { mock() }
    }

    @Test
    fun `given Interactiontype is TOUCH, when element long clicked, should show tooltip`() = runComposeUiTest {
        createComponent(TOUCH)

        onNodeWithText("Content").performMouseInput { longClick() }

        onNodeWithText("Tooltip").assertExists()
        onNodeWithText("Act").assertExists()
    }

    @Test
    fun `given Interactiontype is TOUCH, when tooltip action clicked, should execute action`() = runComposeUiTest {
        val mock = spyk<() -> Unit>()
        createComponent(TOUCH, mock)

        onNodeWithText("Content").performMouseInput { longClick() }
        onNodeWithText("Act").performMouseInput { click() }

        verify(exactly = 1) { mock() }
    }

    private fun ComposeUiTest.createComponent(type: InteractionType, action: () -> Unit = {}) {
        setContent {
            CompositionLocalProvider(LocalInteractionType provides type) {
                ActionableTooltip("Act", action, true, { Text("Tooltip") }) {
                    Text("Content")
                }
            }
        }
    }
}
