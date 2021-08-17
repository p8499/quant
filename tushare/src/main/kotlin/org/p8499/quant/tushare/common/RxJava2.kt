package org.p8499.quant.tushare.common

import io.reactivex.Flowable

inline fun <T : Any, S : Any> Flowable<T>.mapNotNull(crossinline transform: (T) -> S?): Flowable<S> = flatMap {
    val result = transform(it)
    if (result == null) Flowable.empty() else Flowable.just(result)
}
