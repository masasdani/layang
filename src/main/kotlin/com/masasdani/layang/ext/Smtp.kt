package com.masasdani.layang.ext

import io.vertx.core.net.NetSocket

/**
 *
 * @author masasdani
 * @since 7/22/17
 */
enum class Command {
    HELO,
    EHLO,
    AUTH,
    MAIL,
    RCPT,
    DATA,
    RSET,
    VRFY,
    NOOP,
    SIZE,
    QUIT,
    EXIT,
    HELP
}

open class BaseSMTPException(val code: Int, override val message: String): RuntimeException(message) {
    fun response() = "$code $message\n"
}

class BadCommandException(): BaseSMTPException(500, "Unrecognized Command")
class BadParamException(message: String = "Bad command parameter"): BaseSMTPException(501, message)
class BadSquenceException(message: String = "Bad squence of command"): BaseSMTPException(503, message)

fun NetSocket.fail(ex: BaseSMTPException) {
    this.write(ex.response())
}

data class SmtpResponse(val status: Int, val message: String) {
    override fun toString(): String {
        return "$status $message\n"
    }
}

fun NetSocket.response(response: SmtpResponse)
        = this.write(response.toString())

fun NetSocket.success()
        = this.response(SmtpResponse(200, "Success"))

fun NetSocket.status()
        = this.response(SmtpResponse(211, "Layang ready"))

fun NetSocket.ready(domain: String)
        = this.response(SmtpResponse(220, "Layang at $domain, SMTP Service ready"))

fun NetSocket.closing() {
    this.response(SmtpResponse(221, "Bye.."))
    this.close()
}

fun NetSocket.ok(message: String = "OK")
        = this.response(SmtpResponse(250, message))

fun NetSocket.forwarding(email: String)
        = this.response(SmtpResponse(251, "User not local; will forward to $email"))

fun NetSocket.notVerify()
        = this.response(SmtpResponse(252, "Cannot VRFY user, but will accept message and attempt delivery"))

fun NetSocket.starting()
        = this.response(SmtpResponse(252, "Start mail input; end with <CRLF>.<CRLF>"))

fun NetSocket.domainNotAvailable(domain: String)
        = this.response(SmtpResponse(421, "$domain Service not available, closing transmission channel"))

fun NetSocket.mailboxNotAvailable()
        = this.response(SmtpResponse(450, "Requested mail action not taken: mailbox unavailable"))

fun NetSocket.abort()
        = this.response(SmtpResponse(451, "Requested action aborted: local error in processing"))

fun NetSocket.insufficientStorage()
        = this.response(SmtpResponse(452, "Requested action not taken: insufficient system storage"))

fun NetSocket.badCommand()
        = this.response(SmtpResponse(500, "Unrecognised command"))

fun NetSocket.paramError()
        = this.response(SmtpResponse(501, "Syntax error in parameters or arguments"))

fun NetSocket.notImplemented()
        = this.response(SmtpResponse(502, "Command not implemented"))

fun NetSocket.badSquence()
        = this.response(SmtpResponse(503, "Bad sequence of commands"))

fun NetSocket.paramNotImplemented()
        = this.response(SmtpResponse(504, "Command parameter not implemented"))

fun NetSocket.notAcceptEmail(domain: String)
        = this.response(SmtpResponse(521, "$domain does not accept mail (see rfc1846)"))

fun NetSocket.accessDenied()
        = this.response(SmtpResponse(530, "Access denied (???a Sendmailism)"))

fun NetSocket.mailboxUnavailable()
        = this.response(SmtpResponse(550, "Requested action not taken: mailbox unavailable"))

fun NetSocket.notLocal()
        = this.response(SmtpResponse(551, "User not local; please try <forward-path>"))

fun NetSocket.exceededStorage()
        = this.response(SmtpResponse(552, "Requested mail action aborted: exceeded storage allocation"))

fun NetSocket.mailboxNotAllowed()
        = this.response(SmtpResponse(553, "Requested action not taken: mailbox name not allowed"))

fun NetSocket.failed()
        = this.response(SmtpResponse(554, "Transaction failed"))
