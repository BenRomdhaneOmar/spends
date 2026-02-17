package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.framework

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import java.nio.charset.StandardCharsets

@Configuration(proxyBeanMethods = false)
object FrameworkConfiguration {

    @Bean
    fun frameworkSettings() = FrameworkSettings()

    @Bean
    fun internationalizationRepository(
        resourceBundleMessageSource: ResourceBundleMessageSource,
        frameworkSettings: FrameworkSettings
    ) =
        InternationalizationRepository
            .instance(
                resourceBundleMessageSource,
                frameworkSettings.defaultLanguageCode
            )

    @Bean
    fun resourceBundleMessageSource() =
        ResourceBundleMessageSource()
            .apply {
                setBasenames(
                    "i18n/errors"
                )
                setDefaultEncoding(
                    StandardCharsets.UTF_8.name()
                )
                setCacheSeconds(3600)
            }
}