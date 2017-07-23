package com.masasdani.layang.ext

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.rx.java.ObservableFuture
import io.vertx.rx.java.SingleOnSubscribeAdapter
import rx.Observable
import rx.Single

/**
 *
 * @author masasdani
 * @since 6/2/17
 */
inline fun <T : Any> observable(operation: (Handler<AsyncResult<T>>) -> Unit): Observable<T> {
    val future = ObservableFuture<T>()
    operation(future.toHandler())
    return future
}

inline fun <T : Any> single(crossinline operation: (Handler<AsyncResult<T>>) -> Unit): Single<T> {
    return Single.create(SingleOnSubscribeAdapter<T> { fut -> operation(fut) })
}

inline fun <T : Any> observableCall(crossinline operation: () -> T): Observable<T> {
    return Observable.fromCallable {
        operation()
    }
}

fun <T> Future<T>.toObservable(): Observable<T> {
    val observable = ObservableFuture<T>()
    this.setHandler(observable.toHandler())
    return observable
}