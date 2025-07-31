package br.com.gabryel.reginaesanguine.app.util

import android.util.Log

actual class Logger actual constructor(val clazz: Class<*>) {
    actual fun error(text: String) {
        Log.e(clazz.name, text)
    }

    actual fun error(text: String, exception: Throwable) {
        Log.e(clazz.name, text, exception)
    }
}
