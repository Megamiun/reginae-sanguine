package br.com.gabryel.reginaesanguine.cli.components

import com.jakewharton.mosaic.layout.ContentDrawScope
import com.jakewharton.mosaic.layout.DrawModifier
import com.jakewharton.mosaic.modifier.Modifier

data class FillModifier(val filler: Char) : DrawModifier {
    override fun ContentDrawScope.draw() {
        drawRect(filler)
    }
}

fun Modifier.fillWith(filler: Char) = then(FillModifier(filler))
