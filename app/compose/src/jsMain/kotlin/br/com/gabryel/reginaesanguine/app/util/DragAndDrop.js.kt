package br.com.gabryel.reginaesanguine.app.util

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.domDataTransferOrNull
import androidx.compose.ui.geometry.Offset
import org.w3c.dom.DataTransfer

actual fun getTransferData(offset: Offset, data: String): DragAndDropTransferData {
    val transfer = createDataTransfer()
    transfer.setData("text", data)

    return DragAndDropTransferData(transfer)
}

@OptIn(ExperimentalComposeUiApi::class)
actual fun drop(event: DragAndDropEvent, drop: (String) -> Boolean): Boolean {
    val transfer = event.transferData?.domDataTransferOrNull ?: return false

    return drop(transfer.getData("text"))
}

fun createDataTransfer(): DataTransfer =
    js("new DataTransfer()")
