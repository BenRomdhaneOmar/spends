package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.repository

import com.benromdhane.omar.offroadsoft.monad.error.SuccessOrSingleError
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.model.PersistedSpend
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.model.Spend

interface SpendsRepository {

    fun allSpends(): SuccessOrSingleError<Collection<PersistedSpend>, Error>
    fun addSpend(spend: Spend): SuccessOrSingleError<PersistedSpend, Error>

    sealed interface Error {

        sealed interface Business : Error {

            object NoSpendsFound : Business
        }

        @ConsistentCopyVisibility
        data class Infrastructure private constructor(
            val cause: Throwable
        ) : Error {
            companion object {
                fun error(cause: Throwable) = Infrastructure(cause)
            }
        }
    }
}
