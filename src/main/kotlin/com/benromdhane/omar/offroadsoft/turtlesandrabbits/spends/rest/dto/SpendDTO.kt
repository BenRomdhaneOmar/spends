package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.rest.dto

import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.model.Spend
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class SpendDTO(
    val value: Float,
    val date: LocalDate,
    val description: String?
) {

    fun toSpend() =
        Spend
            .Builder
            .instance()
            .withValue(this.value)
            .withDate(this.date)
            .withDescription(this.description)
            .build()
}
