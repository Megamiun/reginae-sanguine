package br.com.gabryel.reginaesanguine.app.storage

import br.com.gabryel.reginaesanguine.viewmodel.auth.Storage

class LocalStorage : Storage {
    override val token = StringLocalStorageProperty("auth_token")
    override val accountId = StringLocalStorageProperty("account_id")
    override val serverUrl = StringLocalStorageProperty("server_url")
}
