package org.p8499.quant.analysis.common

import java.util.*
import java.util.stream.Stream

inline fun <T : Any, reified S : Any> Stream<T>.mapNotNull(crossinline transform: (T) -> S?): Stream<S> = flatMap {
    val result = transform(it)
    if (result == null) Arrays.stream(emptyArray()) else Arrays.stream(arrayOf(result))
}
