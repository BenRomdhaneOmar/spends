package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.repository

import com.benromdhane.omar.offroadsoft.monad.error.SuccessOrSingleError
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.model.PersistedSpend
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.model.Spend
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.service.SpendsService

interface SpendsRepository {

    fun allSpends(): SuccessOrSingleError<Collection<PersistedSpend>, SpendsService.Error>
    fun addSpend(spend: Spend): SuccessOrSingleError<PersistedSpend, SpendsService.Error>
}
