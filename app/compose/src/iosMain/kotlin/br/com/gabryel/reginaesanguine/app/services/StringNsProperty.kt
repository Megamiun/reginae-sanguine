package br.com.gabryel.reginaesanguine.app.services

import br.com.gabryel.reginaesanguine.viewmodel.auth.StorageProperty
import platform.Foundation.NSUserDefaults

class StringNsProperty(private val prefs: NSUserDefaults, private val key: String) :
    StorageProperty<String> {
    override fun save(value: String) {
        prefs.setObject(value, key)
    }

    override fun retrieve() = prefs.stringForKey(key)

    override fun clear() {
        prefs.removeObjectForKey(key)
    }
}
