package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.configuration

import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.repository.InMemorySpendsRepository
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.repository.SpendsRepository
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.service.SpendsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpendsConfiguration {

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
            .instance()
}