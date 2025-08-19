package br.com.gabryel.reginaesanguine.app.util

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset

actual fun drop(event: DragAndDropEvent, drop: (String) -> Boolean): Boolean {
    TODO("Not yet implemented")
}

actual fun getTransferData(offset: Offset, data: String): DragAndDropTransferData {
    TODO("Not yet implemented")
}
