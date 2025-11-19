package br.com.gabryel.reginaesanguine.logging

import android.util.Log
import kotlin.reflect.KClass

actual class Logger actual constructor(val loggerName: String) {
    actual constructor(clazz: KClass<*>) : this(clazz.simpleName.orEmpty())

    actual fun error(text: Any?, exception: Throwable?) {
        Log.e(loggerName, "$text", exception)
    }

    actual fun info(text: Any?, exception: Throwable?) {
        Log.i(loggerName, "$text", exception)
    }
}
