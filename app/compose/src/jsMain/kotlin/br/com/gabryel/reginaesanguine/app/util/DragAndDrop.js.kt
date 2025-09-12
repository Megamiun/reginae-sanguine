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
actual fun getContent(event: DragAndDropEvent) =
    event.transferData?.domDataTransferOrNull?.getData("text")

fun createDataTransfer(): DataTransfer =
    js("new DataTransfer()")
