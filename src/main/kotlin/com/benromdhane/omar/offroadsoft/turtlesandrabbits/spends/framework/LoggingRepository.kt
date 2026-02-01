package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.framework

import java.util.concurrent.locks.ReentrantLock
import java.util.logging.Logger
import kotlin.concurrent.withLock
import kotlin.reflect.KClass

class LoggingRepository private constructor(
    logged: KClass<out Any>
) {

    private val log = Logger.getLogger(logged.qualifiedName)

    companion object {

        private val instances = mutableMapOf<KClass<out Any>, LoggingRepository>()
        private val instanceLock = ReentrantLock()


        fun from(logged: KClass<out Any>): LoggingRepository =
            instanceLock
                .withLock {
                    instances.getOrPut(
                        logged,
                        { LoggingRepository(logged) }
                    )
                }
    }

    fun trace(message: String) {
        this.log
            .finest(
                message
            )
    }

    fun debug(message: String) {
        this.log
            .fine(
                message
            )
    }

    fun info(message: String) {
        this.log
            .info(
                message
            )
    }

    fun warning(message: String) {
        this.log
            .warning(
                message
            )
    }

    fun error(
        message: String,
        throwable: Throwable
    ) {
        this.log
            .severe(
                message
            )
        this.log
            .severe(
                throwable.stackTraceToString()
            )
    }

    fun error(throwable: Throwable) {
        this.log
            .severe(
                throwable.stackTraceToString()
            )
    }
}