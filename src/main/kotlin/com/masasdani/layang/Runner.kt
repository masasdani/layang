package com.masasdani.layang

import com.hazelcast.config.Config
import com.masasdani.layang.ext.*
import io.vertx.core.VertxOptions
import io.vertx.core.cli.CLI
import io.vertx.core.cli.Option
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.logging.SLF4JLogDelegateFactory
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.core.Vertx
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager
import rx.Observable
import java.net.InetAddress

/**
 *
 * @author masasdani
 * @since 5/12/17
 */
open class Runner(var configPath: String) {

    private val DEFAULT_CLUSTER_NAME = "layang"
    private var cluster = false

    private val log = logger(this::class)

    companion object{
        fun init(name: String, args: Array<String>): Runner {
            val cli = CLI.create(name)
                    .addOption(Option()
                            .setShortName("c")
                            .setLongName("conf")
                            .setDefaultValue("application.json"))
                    .addOption(Option()
                            .setShortName("l")
                            .setLongName("logging"))
                    .addOption(Option()
                            .setFlag(true)
                            .setLongName("cluster"))

            val commandLine = cli.parse(args.toList())
            val loggerPath = commandLine.getOptionValue<String>("logging")

            System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory::class.java.name)
            if (!loggerPath.isNullOrEmpty()) System.setProperty("logback.configurationFile", loggerPath)
            val runner = Runner(commandLine.getOptionValue<String>("conf"))
            runner.cluster = commandLine.isFlagEnabled("cluster")
            return runner
        }
    }

    open fun start(handler: (Pair<JsonObject, Vertx>) -> Unit){
        if(cluster) startCluster(handler)
        else startSingle(handler)
    }

    open fun startCluster(handler: (Pair<JsonObject, Vertx>) -> Unit){
        val hazelcastConfig = Config()
        val clusterName = env("CLUSTER_NAME", DEFAULT_CLUSTER_NAME)
        val clusterPassword = env("CLUSTER_PASSWORD", "pass-$clusterName")
        hazelcastConfig.groupConfig.name = clusterName
        hazelcastConfig.groupConfig.password = clusterPassword
        log.info("starting apps on cluster $clusterName")

        // for developing mode only, need to adjust external config or build profile
        hazelcastConfig.networkConfig.join.multicastConfig.isEnabled = false
        hazelcastConfig.networkConfig.join.tcpIpConfig.isEnabled = true
        hazelcastConfig.networkConfig.join.tcpIpConfig.addMember("localhost")

        val clusterManager: ClusterManager = HazelcastClusterManager(hazelcastConfig)
        val vertxOption = VertxOptions().apply {
            this.maxEventLoopExecuteTime = Long.MAX_VALUE
            this.clusterManager = clusterManager
            try {
                val address = InetAddress.getByName(env("HOSTNAME", "localhost")).hostAddress
                this.clusterHost = address
                log.info("Cluster set to use clusterHost ${this.clusterHost}")
            } catch (e: Exception) {
                log.info("Hostname not Found, perhaps you run this app locally!")
            }
        }

        observable<Vertx> { Vertx.clusteredVertx(vertxOption, it) }.flatMap {
            val vertx = it
            val config = jsonConfig(configPath)
            vertx.retrieveConfig(config).map { it to vertx }
        }.subscribe({
            it.apply(handler)
        }, {
            it.printStackTrace()
            System.exit(1)
        }, {
            log.info("deployed all verticles")
        })
    }

    open fun startSingle(handler: (Pair<JsonObject, Vertx>) -> Unit){
        Observable.just(Vertx.vertx()).flatMap {
            val vertx = it
            val config = jsonConfig(configPath)
            vertx.retrieveConfig(config).map { it to vertx }
        }.subscribe({
            it.apply(handler)
        }, {
            it.printStackTrace()
            System.exit(1)
        }, {
            log.info("deployed all verticles")
        })
    }

}