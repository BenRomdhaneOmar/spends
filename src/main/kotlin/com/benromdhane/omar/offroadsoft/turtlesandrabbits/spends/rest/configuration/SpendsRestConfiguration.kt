package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.rest.configuration

import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.configuration.SpendsSettings
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.framework.InternationalizationRepository
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.framework.LoggingRepository
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.rest.handler.SpendsRestHandler
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.service.SpendsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
object SpendsRestConfiguration {

    @Bean
    fun spendSettings() = SpendsSettings()

    @Bean
    fun spendsRestHandler(
        internationalizationRepository: InternationalizationRepository,
        spendsService: SpendsService,
        spendsSettings: SpendsSettings
    ) =
        SpendsRestHandler
            .instance(
                LoggingRepository.from(SpendsRestHandler::class),
                internationalizationRepository,
                spendsService,
                spendsSettings.headers!!.count,
                spendsSettings.headers!!.errors!!.messages,
                spendsSettings.headers!!.errors!!.codes,
                spendsSettings.queries!!.language
            )
}