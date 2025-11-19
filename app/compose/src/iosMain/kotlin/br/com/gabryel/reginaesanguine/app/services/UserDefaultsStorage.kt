package br.com.gabryel.reginaesanguine.app.services

import br.com.gabryel.reginaesanguine.viewmodel.auth.Storage
import platform.Foundation.NSUserDefaults

class UserDefaultsStorage : Storage {
    private val defaults = NSUserDefaults.standardUserDefaults

    override val token = StringNsProperty(defaults, "auth_token")
    override val accountId = StringNsProperty(defaults, "account_id")
    override val serverUrl = StringNsProperty(defaults, "server_url")
}
