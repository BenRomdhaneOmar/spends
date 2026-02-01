package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.configuration

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spends")
data class SpendsSettings(
    @NotNull var headers: Headers? = null,
    @NotNull var queries: Queries? = null
) {

    data class Headers(
        @NotBlank var count: String = "",
        @NotNull var errors: Errors? = null
    ) {

        data class Errors(
            @NotBlank var messages: String = "",
            @NotBlank var codes: String = ""
        )
    }

    data class Queries(
        @NotBlank var language: String = ""
    )
}