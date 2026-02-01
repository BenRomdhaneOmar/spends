package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.framework

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource

@Configuration
class FrameworkConfiguration {

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
            }
}