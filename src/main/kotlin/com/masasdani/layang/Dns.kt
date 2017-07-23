package com.masasdani.layang

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future

/**
 *
 * @author masasdani
 * @since 7/23/17
 */
class Dns : AbstractVerticle() {

    override fun start(startFuture: Future<Void>?) {
        val client = vertx.createDnsClient(53, "8.8.8.8")

        vertx.eventBus().consumer<String>("reverse-lookup").handler { message ->
            client.reverseLookup(message.body(), {
                message.reply(it.result())
            })
        }
    }

}