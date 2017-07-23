package com.masasdani.layang.ext

import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.obj
import io.vertx.config.ConfigRetriever
import io.vertx.core.Vertx
import rx.Observable
import kotlin.reflect.full.isSubclassOf

/**
 *
 * @author masasdani
 * @since 4/29/17
 */
fun jsonConfig(path: String): io.vertx.config.ConfigStoreOptions {
    return io.vertx.kotlin.config.ConfigStoreOptions(
            type = "file",
            format = "json",
            config = io.vertx.kotlin.core.json.json {
                obj("path" to path)
            }
    )
}

fun envConfig(): io.vertx.config.ConfigStoreOptions {
    return io.vertx.kotlin.config.ConfigStoreOptions(type = "env")
}

inline fun <reified T : Any> env(key: String, defaultValue: T? = null): T {
    val value: String? = System.getenv(key)
    return if (value != null) {
        if (T::class.isSubclassOf(String::class)) {
            value as T
        } else if (T::class.isSubclassOf(Int::class)) {
            value.toInt() as T
        } else if (T::class.isSubclassOf(Double::class)) {
            value.toDouble() as T
        } else if (T::class.isSubclassOf(Boolean::class)) {
            value.toBoolean() as T
        } else {
            throw IllegalArgumentException("${T::class.qualifiedName} Not Supported")
        }
    } else defaultValue ?: throw IllegalArgumentException("Illegal: $key not found and default value is null!")
}

fun Vertx.retrieveConfig(vararg stores: ConfigStoreOptions): Observable<JsonObject> {
    val options = ConfigRetrieverOptions()
    options.stores = stores.toList().plus(envConfig())
    val retriever = ConfigRetriever.create(this, options)
    return observable { retriever.getConfig(it) }
}

