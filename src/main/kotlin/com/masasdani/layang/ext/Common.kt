package com.masasdani.layang.ext

import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import kotlin.reflect.KClass

/**
 *
 * @author masasdani
 * @since 4/27/17
 */
fun logger(clz: KClass<*>): Logger {
    return LoggerFactory.getLogger(clz.qualifiedName)
}
