package br.com.gabryel.reginaesanguine.app.util

import android.util.Log
import kotlin.reflect.KClass

actual class Logger actual constructor(val clazz: KClass<*>) {
    actual fun error(text: String) {
        Log.e(clazz.simpleName, text)
    }

    actual fun error(text: String, exception: Throwable) {
        Log.e(clazz.simpleName, text, exception)
    }
}
