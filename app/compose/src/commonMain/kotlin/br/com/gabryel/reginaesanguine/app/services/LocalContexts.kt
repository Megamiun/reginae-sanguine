package br.com.gabryel.reginaesanguine.app.services

import androidx.compose.runtime.staticCompositionLocalOf
import br.com.gabryel.reginaesanguine.app.util.InteractionType

val LocalInteractionType = staticCompositionLocalOf<InteractionType> {
    error("CompositionLocal LocalInteractionType not present")
}
