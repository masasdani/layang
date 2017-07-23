package com.masasdani.layang

import com.masasdani.layang.ext.deployVerticle

/**
 *
 * @author masasdani
 * @since 5/22/17
 */
class Layang {

    companion object {
        const val APP_NAME = "layang"

        @JvmStatic fun main(args: Array<String>) {
            Runner.init(APP_NAME, args).start { (config, vertx) ->

                vertx.deployVerticle(Dns(), config)

                vertx.deployVerticle(SMTPServer(port = 2525), config)

            }
        }
    }

}