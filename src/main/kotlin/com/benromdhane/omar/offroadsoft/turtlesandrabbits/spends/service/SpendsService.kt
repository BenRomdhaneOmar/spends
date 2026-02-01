package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.service

import com.benromdhane.omar.offroadsoft.monad.error.SuccessOrSingleError
import com.benromdhane.omar.offroadsoft.monad.error.asSuccessOrSingleError
import com.benromdhane.omar.offroadsoft.monad.evaluation.EvaluateOneElement
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.model.Spend
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.repository.SpendsRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Clock

class SpendsService private constructor(
    private val spendsRepository: SpendsRepository
) {

    fun addSpend(spend: Spend) =
        evaluateSpend(spend)
            .flatMapSuccess {
                this.spendsRepository
                    .addSpend(it)
                    .mapErrorToServiceError()
            }

    fun allSpends() =
        this.spendsRepository
            .allSpends()
            .mapErrorToServiceError()

    companion object {

        private val instanceLock = ReentrantLock()
        private var instance: SpendsService? = null

        fun instance(
            spendsRepository: SpendsRepository
        ) =
            instanceLock
                .withLock {
                    instance
                        ?: SpendsService(
                            spendsRepository
                        )
                            .also { instance = it }
                }

        private fun evaluateSpend(spend: Spend) =
            EvaluateOneElement
                .element<_, Error>(spend)
                .addEvaluation(
                    check = { evaluateSpendDate(it.date) },
                    error = { Error.Business.FutureSpend }
                )
                .evaluate()
                .asSuccessOrSingleError()
                .mapError { it.first() }

        private fun evaluateSpendDate(spendDate: LocalDate) =
            Clock.System.todayIn(TimeZone.currentSystemDefault()) >= spendDate

        private fun SpendsRepository.Error.toServiceError() =
            when (this) {
                SpendsRepository.Error.Business.NoSpendsFound -> Error.Business.NoSpendsFound
                is SpendsRepository.Error.Infrastructure -> Error.Infrastructure.error(this.cause)
            }

        private fun <SUCCESS : Any> SuccessOrSingleError<SUCCESS, SpendsRepository.Error>.mapErrorToServiceError() =
            this.mapError { it.toServiceError() }
    }

    sealed interface Error {

        sealed interface Business : Error {

            object NoSpendsFound : Business
            object FutureSpend : Business
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
