package br.com.gabryel.reginaesanguine.app.ui

import android.content.ClipData
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.geometry.Offset
import br.com.gabryel.reginaesanguine.domain.Card

actual fun getTransferData(offset: Offset, data: String) =
    DragAndDropTransferData(ClipData.newPlainText("cardId", data))

actual fun drop(event: DragAndDropEvent, drop: (String) -> Boolean) =
    drop(event.toAndroidDragEvent().clipData.getItemAt(0).text.toString())
