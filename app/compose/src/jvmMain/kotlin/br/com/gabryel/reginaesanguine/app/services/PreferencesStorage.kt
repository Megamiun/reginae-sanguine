package br.com.gabryel.reginaesanguine.app.services

import br.com.gabryel.reginaesanguine.viewmodel.auth.Storage
import java.util.prefs.Preferences

class PreferencesStorage : Storage {
    private val prefs = Preferences.userNodeForPackage(PreferencesStorage::class.java)

    override val token = StringPreferencesProperty(prefs, "auth_token")
    override val accountId = StringPreferencesProperty(prefs, "account_id")
    override val serverUrl = StringPreferencesProperty(prefs, "server_url")
}
