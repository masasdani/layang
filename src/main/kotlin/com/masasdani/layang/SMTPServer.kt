package com.masasdani.layang

import com.masasdani.layang.ext.*
import io.vertx.core.Future

/**
 *
 * @author masasdani
 * @since 5/22/17
 */
class SMTPServer
constructor(
        val host: String = "localhost",
        val port: Int = 25
): AbstractTcpServer() {

    private val log = logger(SMTPServer::class)

    override fun start(startFuture: Future<Void>?) {
        createNetServer({
            val conn = Connection(vertx, this)
            conn.createHandler()
        }, host, port).handle({
            log.info("smtp server created on $host $port")
            startFuture?.complete()
        }, {
            log.error("failed to start smtp server", it)
            startFuture?.fail(it)
        })
    }

}