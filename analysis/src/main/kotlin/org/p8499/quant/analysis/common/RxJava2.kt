package org.p8499.quant.analysis.common

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.parallel.ParallelFlowable
import io.reactivex.schedulers.Schedulers

inline fun <T : Any, S : Any> Observable<T>.mapNotNull(crossinline transform: (T) -> S?): Observable<S> = flatMap {
    val result = transform(it)
    if (result == null) Observable.empty() else Observable.just(result)
}

inline fun <T : Any, S : Any> Flowable<T>.mapNotNull(crossinline transform: (T) -> S?): Flowable<S> = flatMap {
    val result = transform(it)
    if (result == null) Flowable.empty() else Flowable.just(result)
}

inline fun <T : Any, S : Any> ParallelFlowable<T>.mapNotNull(crossinline transform: (T) -> S?): ParallelFlowable<S> = flatMap {
    val result = transform(it)
    if (result == null) Flowable.empty() else Flowable.just(result)
}

fun <T : Any, S : Any> Iterable<T>.parallelMap(transform: (T) -> S): List<S> = Flowable.fromIterable(this).parallel(4).runOn(Schedulers.io()).map(transform).sequential().blockingIterable().toList()