package com.masasdani.layang.ext

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.net.NetServer
import io.vertx.core.net.NetSocket

/**
 *
 * @author masasdani
 * @since 7/21/17
 */
abstract class AbstractTCPHandler(val handlers: NetSocket.() -> Unit) {
    abstract val handler: NetSocket
    fun create(): NetSocket {
        return handler.apply {
            handlers()
        }
    }
}

abstract class AbstractTcpServer: AbstractVerticle() {

    fun createNetServer(handlers: NetSocket.() -> Unit,
                        host: String = "localhost",
                        port: Int = 25): Future<NetServer> {
        val future = Future.future<NetServer>()
        vertx.createNetServer().connectHandler {
            it.apply { handlers() }
        }.listen(port, host, future.completer())
        return future
    }

}