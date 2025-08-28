@file:OptIn(ExperimentalComposeUiApi::class)

package br.com.gabryel.reginaesanguine.app.util

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset

actual fun getTransferData(offset: Offset, data: String): DragAndDropTransferData {
    TODO()
}

actual fun drop(event: DragAndDropEvent, drop: (String) -> Boolean): Boolean {
    TODO()
}
