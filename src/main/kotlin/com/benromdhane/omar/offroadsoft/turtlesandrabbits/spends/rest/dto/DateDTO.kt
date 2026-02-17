package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.rest.dto

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.serialization.Serializable

@Serializable
data class DateDTO(
    val dayOfMonth: Int,
    val month: Int,
    val year: Int
) {

    constructor(localDate: LocalDate) :
            this(
                localDate.day,
                localDate.month.number,
                localDate.year
            )

    fun toLocalDate() =
        LocalDate(
            this.year,
            this.month,
            this.dayOfMonth
        )

}

fun LocalDate.toDateDTO() = DateDTO(this)