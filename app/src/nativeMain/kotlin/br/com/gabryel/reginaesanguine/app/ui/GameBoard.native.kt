package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset
import br.com.gabryel.reginaesanguine.domain.Card

actual fun drop(event: DragAndDropEvent, drop: (String) -> Boolean): Boolean {
    TODO("Not yet implemented")
}

// TODO Analyse failed import
actual fun getTransferData(offset: Offset, card: Card): DragAndDropTransferData {
    TODO("Not yet implemented")
}
