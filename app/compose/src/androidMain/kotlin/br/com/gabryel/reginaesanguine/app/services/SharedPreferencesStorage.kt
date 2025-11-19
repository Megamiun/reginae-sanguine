package br.com.gabryel.reginaesanguine.app.services

import android.content.Context
import android.content.Context.MODE_PRIVATE
import br.com.gabryel.reginaesanguine.viewmodel.auth.Storage

class SharedPreferencesStorage(context: Context) : Storage {
    private val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

    override val token = StringSharedPreferencesProperty(prefs, "auth_token")
    override val accountId = StringSharedPreferencesProperty(prefs, "account_id")
    override val serverUrl = StringSharedPreferencesProperty(prefs, "server_url")

    companion object Companion {
        private const val PREFS_NAME = "reginae_auth"
    }
}
