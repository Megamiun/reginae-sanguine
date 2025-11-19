package br.com.gabryel.reginaesanguine.viewmodel.auth

interface Storage {
    val token: StorageProperty<String>
    val accountId: StorageProperty<String>
    val serverUrl: StorageProperty<String>
}
