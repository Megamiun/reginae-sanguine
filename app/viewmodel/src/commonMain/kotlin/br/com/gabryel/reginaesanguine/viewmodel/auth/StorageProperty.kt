package br.com.gabryel.reginaesanguine.viewmodel.auth

interface StorageProperty<T> {
    fun retrieve(): T?

    fun save(value: T)

    fun clear()
}
