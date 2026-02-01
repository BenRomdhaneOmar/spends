package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.framework

import com.benromdhane.omar.offroadsoft.monad.error.Try
import org.springframework.context.support.ResourceBundleMessageSource
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class InternationalizationRepository private constructor(
    private val resourceBundleMessageSource: ResourceBundleMessageSource,
    private val defaultLanguage: String
) {

    fun forCode(
        messageCode: String,
        languageCode: String? = null
    ): String =
        Try.trying {
            this.resourceBundleMessageSource
                .getMessage(
                    messageCode,
                    null,
                    Locale.of(languageCode ?: defaultLanguage)
                )
        }
            .toMaybeSuccess()
            .or(messageCode)

    companion object {

        private val instanceLock = ReentrantLock()
        private var instance: InternationalizationRepository? = null

        fun instance(
            resourceBundleMessageSource: ResourceBundleMessageSource,
            defaultLanguage: String
        ) =
            instanceLock
                .withLock {
                    instance
                        ?: InternationalizationRepository(
                            resourceBundleMessageSource,
                            defaultLanguage
                        )
                            .also { instance = it }
                }
    }
}