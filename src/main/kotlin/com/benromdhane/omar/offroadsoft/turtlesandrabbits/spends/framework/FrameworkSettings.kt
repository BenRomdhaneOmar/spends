package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.framework

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "settings")
data class FrameworkSettings(
    @NotBlank var defaultLanguageCode: String = ""
)