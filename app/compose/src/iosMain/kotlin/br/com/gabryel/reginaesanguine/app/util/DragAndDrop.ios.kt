package br.com.gabryel.reginaesanguine.app.util

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset
import br.com.gabryel.reginaesanguine.logging.Logger
import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSItemProvider
import platform.Foundation.NSString
import platform.Foundation.create
import platform.UIKit.UIDragItem
import platform.UniformTypeIdentifiers.UTTypeUTF8PlainText

private val logger = Logger("DragAndDrop")

// Workaround: Store dragged data globally since extracting multiple times from NSItemProvider doesn't work
private var currentDragData: String? = null

@OptIn(ExperimentalComposeUiApi::class, BetaInteropApi::class)
actual fun getTransferData(offset: Offset, data: String): DragAndDropTransferData {
    val nsString = NSString.create(string = data)
    val itemProvider = NSItemProvider(item = nsString, typeIdentifier = UTTypeUTF8PlainText.identifier)
    val dragItem = UIDragItem(itemProvider)

    currentDragData = data
    return DragAndDropTransferData(listOf(dragItem))
}

@OptIn(ExperimentalComposeUiApi::class)
actual fun getContent(event: DragAndDropEvent): String? {
    val items = event.items
    if (items.isEmpty()) return null

    return currentDragData ?: run {
        logger.error("iOS drag and drop: No cached data found. NSItemProvider doesn't allow multiple read operations.")
        null
    }
}
