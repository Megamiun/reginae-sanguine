package br.com.gabryel.reginaesanguine.app.util

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset

expect fun drop(event: DragAndDropEvent, drop: (String) -> Boolean): Boolean

expect fun getTransferData(offset: Offset, data: String): DragAndDropTransferData
