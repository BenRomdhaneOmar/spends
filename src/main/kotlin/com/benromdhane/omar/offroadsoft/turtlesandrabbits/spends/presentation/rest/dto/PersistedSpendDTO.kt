package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.presentation.rest.dto

import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.model.PersistedSpend
import kotlinx.serialization.Serializable

@Serializable
data class PersistedSpendDTO(
    val identifier: String,
    val value: Float,
    val date: DateDTO,
    val description: String?
) {
    constructor(persistedSpend: PersistedSpend) :
            this(
                persistedSpend.identifier,
                persistedSpend.value,
                persistedSpend.date.toDateDTO(),
                persistedSpend.description.orNull()
            )
}
