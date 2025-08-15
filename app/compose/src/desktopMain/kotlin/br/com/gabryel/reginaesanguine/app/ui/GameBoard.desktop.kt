@file:OptIn(ExperimentalComposeUiApi::class)

package br.com.gabryel.reginaesanguine.app.ui

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferAction.Companion.Move
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.DragAndDropTransferable
import androidx.compose.ui.geometry.Offset
import br.com.gabryel.reginaesanguine.domain.Card
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.dnd.DropTargetDropEvent

actual fun getTransferData(offset: Offset, card: Card): DragAndDropTransferData {
    val transferable = DragAndDropTransferable(StringSelection(card.id))

    return DragAndDropTransferData(transferable, listOf(Move), offset)
}

actual fun drop(event: DragAndDropEvent, drop: (String) -> Boolean): Boolean {
    val data = event.nativeEvent as? DropTargetDropEvent
        ?: return false

    return drop(data.transferable.getTransferData(DataFlavor.stringFlavor).toString())
}
