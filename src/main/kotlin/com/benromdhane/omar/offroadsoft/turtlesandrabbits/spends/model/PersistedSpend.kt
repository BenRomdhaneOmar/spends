package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.model

import com.benromdhane.omar.offroadsoft.monad.Maybe
import com.benromdhane.omar.offroadsoft.monad.evaluation.EvaluateOneElement
import kotlinx.datetime.LocalDate

@ConsistentCopyVisibility
data class PersistedSpend private constructor(
    private val spend: Spend,
    val identifier: String,
    val value: Float = spend.value,
    val date: LocalDate = spend.date,
    val description: Maybe<String> = spend.description
) {

    companion object {

        fun of(
            identifier: String,
            spend: Spend
        ) =
            EvaluateOneElement
                .element<_, Error>(identifier)
                .addEvaluation(check = ::evaluateIdentifier, error = { Error.IdentifierEmpty })
                .evaluate()
                .mapSuccess {
                    PersistedSpend(
                        spend,
                        it
                    )
                }

        private fun evaluateIdentifier(identifier: String) = identifier.isNotBlank()
    }

    sealed interface Error {

        object IdentifierEmpty : Error
    }
}
