@file:OptIn(ExperimentalComposeUiApi::class)

package br.com.gabryel.reginaesanguine.app.util

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferAction.Companion.Move
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.DragAndDropTransferable
import androidx.compose.ui.geometry.Offset
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.dnd.DropTargetDropEvent

actual fun getTransferData(offset: Offset, data: String): DragAndDropTransferData {
    val transferable = DragAndDropTransferable(StringSelection(data))

    return DragAndDropTransferData(transferable, listOf(Move), offset)
}

actual fun getContent(event: DragAndDropEvent) =
    (event.nativeEvent as? DropTargetDropEvent)?.transferable
        ?.getTransferData(DataFlavor.stringFlavor)?.toString()
