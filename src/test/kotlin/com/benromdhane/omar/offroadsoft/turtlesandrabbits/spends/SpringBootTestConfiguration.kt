package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends

import org.springframework.boot.SpringBootConfiguration
import org.springframework.context.annotation.ComponentScan

@SpringBootConfiguration(proxyBeanMethods = false)
@ComponentScan("com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.**")
object SpringBootTestConfiguration {
}