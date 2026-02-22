package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.configuration

import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.repository.SpendsRepository
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.service.SpendsService
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.infrastructure.repository.InMemorySpendsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
object SpendsConfiguration {

    @Bean
    fun spendsService(
        spendsRepository: SpendsRepository
    ) =
        SpendsService
            .instance(
                spendsRepository
            )

    @Bean
    fun inMemorySpendsRepository() =
        InMemorySpendsRepository
}