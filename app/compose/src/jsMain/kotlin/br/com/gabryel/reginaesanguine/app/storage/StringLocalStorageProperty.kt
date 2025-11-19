package br.com.gabryel.reginaesanguine.app.storage

import br.com.gabryel.reginaesanguine.viewmodel.auth.StorageProperty
import kotlinx.browser.localStorage

class StringLocalStorageProperty(private val key: String) : StorageProperty<String> {
    override fun save(value: String) {
        localStorage.setItem(key, value)
    }

    override fun retrieve() = localStorage.getItem(key)

    override fun clear() {
        localStorage.removeItem(key)
    }
}
