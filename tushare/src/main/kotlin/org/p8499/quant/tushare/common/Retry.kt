package org.p8499.quant.tushare.common

fun <T> tryInvoke(method: () -> T, error: () -> Unit): T {
    try {
        return method()
    } catch (e: Throwable) {
        error()
        throw e
    }
}