package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.rest.dto

import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.model.Spend
import kotlinx.serialization.Serializable

@Serializable
data class SpendDTO(
    val value: Float,
    val date: DateDTO,
    val description: String? = null
) {

    fun toSpend() =
        Spend
            .Builder
            .instance()
            .withValue(this.value)
            .withDate(this.date.toLocalDate())
            .withDescription(this.description)
            .build()
}
