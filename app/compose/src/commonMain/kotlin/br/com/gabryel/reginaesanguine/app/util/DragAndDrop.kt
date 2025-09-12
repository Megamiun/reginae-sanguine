package br.com.gabryel.reginaesanguine.app.util

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset

expect fun getContent(event: DragAndDropEvent): String?

expect fun getTransferData(offset: Offset, data: String): DragAndDropTransferData
