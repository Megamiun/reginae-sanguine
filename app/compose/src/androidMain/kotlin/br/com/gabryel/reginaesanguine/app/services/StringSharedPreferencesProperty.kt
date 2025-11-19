package br.com.gabryel.reginaesanguine.app.services

import android.content.SharedPreferences
import androidx.core.content.edit
import br.com.gabryel.reginaesanguine.viewmodel.auth.StorageProperty

class StringSharedPreferencesProperty(private val prefs: SharedPreferences, private val key: String) :
    StorageProperty<String> {
    override fun save(value: String) {
        prefs.edit { putString(key, value) }
    }

    override fun retrieve() = prefs.getString(key, null)

    override fun clear() {
        prefs.edit { remove(key) }
    }
}
