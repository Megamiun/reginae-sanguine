package br.com.gabryel.reginaesanguine.app.services

import br.com.gabryel.reginaesanguine.viewmodel.auth.StorageProperty
import java.util.prefs.Preferences

class StringPreferencesProperty(private val prefs: Preferences, private val key: String) : StorageProperty<String> {
    override fun save(value: String) {
        prefs.put(key, value)
    }

    override fun retrieve() = prefs.get(key, null)

    override fun clear() {
        prefs.remove(key)
    }
}
