package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.rest.dto

import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.model.PersistedSpend
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class PersistedSpendDTO(
    val identifier: String,
    val value: Float,
    val date: LocalDate,
    val description: String?
) {
    constructor(persistedSpend: PersistedSpend) :
            this(
                persistedSpend.identifier,
                persistedSpend.value,
                persistedSpend.date,
                persistedSpend.description.orNull()
            )
}
