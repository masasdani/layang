package com.masasdani.layang

import com.masasdani.layang.ext.*
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket
import rx.Observable

/**
 *
 * @author masasdani
 * @since 7/21/17
 */
class Connection
constructor(
        private val vertx: Vertx,
        private val socket: NetSocket
) {

    enum class Stage {
        CMD,
        DATA,
        PAUSE,
        DISCONNECTING,
        DISCONNECTED
    }

    private val log = logger(Connection::class)

    private var stage = Stage.PAUSE
    private var capabilities = arrayListOf<Command>()
    private val data = Buffer.buffer()

    fun createHandler(): NetSocket = socket.handler { buffer ->
        try {
            when (stage) {
                Stage.DATA -> handleData(buffer)
                else -> handleCommand(buffer)
            }
        } catch (ex: Exception) {
            if (ex is BaseSMTPException) socket.fail(ex)
            else log.error(ex.message, ex)
        }
    }

    private fun handleCommand(buffer: Buffer) {
        val command = buffer.toString().trim()
        if (command.length < 4) throw BadCommandException()
        when (command.substring(0, 4).toUpperCase()) {
            "QUIT", "EXIT" -> socket.closing()
            "DATA" -> handleDataCommand()
            "HELO" -> handleHeloCommand(command)
            "MAIL" -> handleMailCommand(command)
            "RCPT" -> handleRcptCommand(command)
            "VRFY" -> handleVrfyCommand(command)
            "RSET" -> handleRsetCommand(command)
            "NOOP" -> handleNoopCommand(command)
            "SIZE" -> handleSizeCommand(command)
            "HELP" -> handleHelpCommand(command)
            "EHLO" -> handleEhloCommand(command)
            else -> throw BadCommandException()
        }
    }

    private fun handleHelpCommand(command: String) {

    }

    private fun handleSizeCommand(command: String) {

    }

    private fun handleNoopCommand(command: String) {

    }

    private fun handleRsetCommand(command: String) {

    }

    private fun handleVrfyCommand(command: String) {

    }

    private fun handleRcptCommand(command: String) {

    }

    private fun handleMailCommand(command: String) {

    }

    private fun handleHeloCommand(command: String) {
        val param = command.substring(4)
        if (param.isNullOrEmpty()) throw BadParamException("HELO requires domain/address - see RFC-2821 4.1.1.1")
        socket.ready(param.trim())
    }

    private fun handleEhloCommand(command: String) {
        val param = command.substring(4)
        if (param.isNullOrEmpty()) throw BadParamException("EHLO requires domain/address - see RFC-2821 4.1.1.1")
        socket.ready(param.trim())
    }

    private fun handleDataCommand() {
        stage = Stage.DATA
        socket.starting()
    }

    private fun handleData(buffer: Buffer) {
        if (buffer.toString().trim() == ".") {
            socket.ok()
            stage = Stage.CMD
        } else {
            data.appendBuffer(buffer)
        }
    }

}