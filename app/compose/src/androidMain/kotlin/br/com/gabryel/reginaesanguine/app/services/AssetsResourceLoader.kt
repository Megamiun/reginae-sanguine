package br.com.gabryel.reginaesanguine.app.services

import android.content.Context

class AssetsResourceLoader(val context: Context) : ResourceLoader {
    override suspend fun load(path: String): ByteArray = context.assets.open(path).readBytes()
}
