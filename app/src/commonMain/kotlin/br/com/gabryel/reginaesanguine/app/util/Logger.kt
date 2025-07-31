package br.com.gabryel.reginaesanguine.app.util

expect class Logger(clazz: Class<*>) {
    fun error(text: String)

    fun error(text: String, exception: Throwable)
}
