package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.framework

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = ["com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.**"],
    proxyBeanMethods = false
)
object SpendsApplication

fun main(args: Array<String>) {
    runApplication<SpendsApplication>(*args)
}

