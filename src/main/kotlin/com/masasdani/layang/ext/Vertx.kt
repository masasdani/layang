package com.masasdani.layang.ext

import io.vertx.core.DeploymentOptions
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

/**
 * Created by masasdani on 6/24/17.
 */
fun Vertx.deployVerticle(verticle: Verticle, config: JsonObject, worker: Boolean = false) {
    val options = DeploymentOptions().apply {
        this.config = config
        if (worker) {
            this.isWorker = true
            this.maxWorkerExecuteTime = Long.MAX_VALUE
        }
    }
    this.deployVerticle(verticle, options)
}