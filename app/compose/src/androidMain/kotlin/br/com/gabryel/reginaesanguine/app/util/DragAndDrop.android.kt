package br.com.gabryel.reginaesanguine.app.util

import android.content.ClipData
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.geometry.Offset

actual fun getTransferData(offset: Offset, data: String) =
    DragAndDropTransferData(ClipData.newPlainText("CARD:$data", data))

actual fun getContent(event: DragAndDropEvent) =
    event.toAndroidDragEvent().clipDescription?.label
        ?.split(":")?.last()
