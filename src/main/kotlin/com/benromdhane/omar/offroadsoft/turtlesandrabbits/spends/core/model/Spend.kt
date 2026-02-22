package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.model

import com.benromdhane.omar.offroadsoft.monad.Maybe
import com.benromdhane.omar.offroadsoft.monad.asMaybe
import com.benromdhane.omar.offroadsoft.monad.evaluation.EvaluateTwoElements
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

@ConsistentCopyVisibility
data class Spend private constructor(
    val value: Float,
    val date: LocalDate,
    val description: Maybe<String>
) {

    private companion object {

        fun of(
            value: Float,
            date: LocalDate,
            description: String?
        ) =
            Spend(
                value,
                date,
                description
                    .asMaybe()
                    .filter { it.isNotBlank() }
            )
    }

    class Builder private constructor(
        private val value: Float,
        private val date: LocalDate,
        private val description: String?
    ) {
        companion object {

            fun instance() =
                Builder(
                    0F,
                    Clock.System.todayIn(TimeZone.currentSystemDefault()),
                    null
                )

            private fun evaluateValue(value: Float) =
                value.isFinite() and
                        (value > 0)

            private fun evaluateDescription(description: String) =
                description.isBlank() or
                        (description.trim().length >= 3)
        }

        fun withValue(value: Float) =
            Builder(
                value,
                this.date,
                this.description
            )

        fun withDate(date: LocalDate) =
            Builder(
                this.value,
                date,
                this.description
            )

        fun withDescription(description: String?) =
            Builder(
                this.value,
                this.date,
                description
            )

        fun build() =
            EvaluateTwoElements
                .first<_, Error>(this.value)
                .addEvaluation(check = ::evaluateValue, error = { Error.ValueZeroOrNegative })
                .second(this.description ?: "")
                .addEvaluation(
                    check = ::evaluateDescription,
                    error = { Error.DescriptionLengthLessThanThreeCharacters })
                .evaluate()
                .mapSuccess {
                    of(
                        it.firstElement,
                        this.date,
                        it.secondElement
                    )
                }
    }

    sealed interface Error {

        object ValueZeroOrNegative : Error
        object DescriptionLengthLessThanThreeCharacters : Error
    }
}
