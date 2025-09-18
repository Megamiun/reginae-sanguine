package br.com.gabryel.reginaesanguine.cli.components

import androidx.compose.runtime.Composable
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Arrangement.Absolute.spacedBy
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text

@Composable
fun <T> OptionChooser(
    header: String,
    options: List<T>,
    selectedIndex: Int,
    describe: T.() -> String = { toString() },
    onIndexChange: (Int) -> Unit,
    onSelect: (T) -> Unit,
) {
    Column(
        Modifier.onKeyEvent { keyEvent ->
            val choice = when (keyEvent) {
                KeyEvent("ArrowLeft"), KeyEvent("ArrowUp") -> selectedIndex - 1
                KeyEvent("ArrowRight"), KeyEvent("ArrowDown") -> selectedIndex + 1
                KeyEvent("Enter") -> {
                    onSelect(options[selectedIndex])
                    return@onKeyEvent false
                }
                else -> return@onKeyEvent false
            }.mod(options.size)

            onIndexChange(choice)

            true
        },
    ) {
        Text(header)
        options.forEachIndexed { index, option ->
            Row(Modifier, spacedBy(1)) {
                Text(if (selectedIndex == index) "*" else " ")
                Text(option.describe())
            }
        }
    }
}
