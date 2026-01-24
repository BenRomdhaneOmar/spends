package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.framework

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.**"])
class SpendsApplication

fun main(args: Array<String>) {
    runApplication<SpendsApplication>(*args)
}

