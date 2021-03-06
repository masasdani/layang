package com.masasdani.layang.ext

import io.vertx.core.*

/**
 *
 */
inline fun <T> Vertx.async(crossinline operation: () -> T): Future<T> {
    val future = Future.future<T>()
    this.executeBlocking<T>(io.vertx.core.Handler { f ->
        try {
            f.complete(operation())
        } catch (e: Exception) {
            f.fail(e)
        }
    }, future.completer())
    return future
}

inline fun <T> futureTask(operation: (Handler<AsyncResult<T>>) -> Unit): Future<T> {
    val future = Future.future<T>()
    operation(future.completer())
    return future
}

fun all(vararg futures: Future<*>): CompositeFuture {
    return CompositeFuture.all(futures.asList())
}

fun any(vararg futures: Future<*>): CompositeFuture {
    return CompositeFuture.any(futures.asList())
}

infix fun <T> Future<T>.handle(operation: (AsyncResult<T>) -> Unit): Future<T> {
    return this.setHandler(operation)
}

fun <T> Future<T>.handle(success: (T) -> Unit, failed: (Throwable) -> Unit): Future<T> {
    return this.setHandler {
        if (it.succeeded()) {
            success(it.result())
        } else {
            failed(it.cause())
        }
    }
}