package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.presentation.rest.handler

import com.benromdhane.omar.offroadsoft.monad.error.asSuccessOrSingleError
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.model.PersistedSpend
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.model.Spend
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.service.SpendsService
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.framework.InternationalizationRepository
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.framework.LoggingRepository
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.presentation.rest.dto.PersistedSpendDTO
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.presentation.rest.dto.SpendDTO
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.jvm.optionals.getOrNull

class SpendsRestHandler private constructor(
    private val loggingRepository: LoggingRepository,
    private val internationalizationRepository: InternationalizationRepository,
    private val spendsService: SpendsService,
    private val countHeaderName: String,
    private val errorHeaderName: String,
    private val errorCodeHeaderName: String,
    private val languageQueryParameterName: String
) {

    fun fetchAllSpends(serverRequest: ServerRequest): ServerResponse =
        this.spendsService
            .allSpends()
            .mapSuccess { it.toDTOs() }
            .fold(
                { it.toSuccessServerResponse(this.countHeaderName) },
                { it.toErrorServerResponse(serverRequest.toRequestLanguage()) }
            )

    fun addSpend(serverRequest: ServerRequest): ServerResponse =
        serverRequest
            .body<SpendDTO>()
            .toSpend()
            .asSuccessOrSingleError()
            .mapError { it.toErrorServerResponse(serverRequest.toRequestLanguage()) }
            .flatMapSuccess {
                addSpend(
                    it,
                    serverRequest.toRequestLanguage()
                )
            }
            .fold(
                { it.toSuccessServerResponse() },
                { it }
            )

    private fun addSpend(
        spend: Spend,
        languageCode: String?
    ) =
        this.spendsService
            .addSpend(spend)
            .mapSuccess(::PersistedSpendDTO)
            .mapError { it.toErrorServerResponse(languageCode) }

    private fun SpendsService.Error.toErrorServerResponse(
        languageCode: String? = null
    ) =
        this.toErrorServerResponse(
            this@SpendsRestHandler.countHeaderName,
            this@SpendsRestHandler.errorHeaderName,
            this@SpendsRestHandler.errorCodeHeaderName,
            this@SpendsRestHandler.loggingRepository,
            this@SpendsRestHandler.internationalizationRepository,
            languageCode
        )

    private fun Collection<Spend.Error>.toErrorServerResponse(
        languageCode: String? = null
    ) =
        this.toErrorServerResponse(
            this@SpendsRestHandler.internationalizationRepository,
            this@SpendsRestHandler.errorHeaderName,
            this@SpendsRestHandler.errorCodeHeaderName,
            languageCode
        )

    private fun ServerRequest.toRequestLanguage() =
        this.toRequestLanguage(
            this@SpendsRestHandler.languageQueryParameterName
        )

    companion object {
        private val instanceLock = ReentrantLock()
        private var instance: SpendsRestHandler? = null

        fun instance(
            loggingRepository: LoggingRepository,
            internationalizationRepository: InternationalizationRepository,
            spendsService: SpendsService,
            countHeaderName: String,
            errorHeaderName: String,
            errorCodeHeaderName: String,
            languageQueryParameterName: String
        ) =
            instanceLock
                .withLock {
                    instance
                        ?: SpendsRestHandler(
                            loggingRepository,
                            internationalizationRepository,
                            spendsService,
                            countHeaderName,
                            errorHeaderName,
                            errorCodeHeaderName,
                            languageQueryParameterName
                        )
                            .also { instance = it }
                }

        private fun Collection<PersistedSpend>.toDTOs() =
            this.map(::PersistedSpendDTO)

        private fun <ELEMENT> Collection<ELEMENT>.toSuccessServerResponse(
            countHeaderName: String
        ) =
            ServerResponse
                .status(HttpStatus.OK)
                .header(countHeaderName, this.size.toString())
                .body(this)

        private fun <ELEMENT> ELEMENT.toSuccessServerResponse() =
            this
                ?.let {
                    ServerResponse
                        .status(HttpStatus.OK)
                        .body(it)
                }
                ?: ServerResponse
                    .status(HttpStatus.NO_CONTENT)
                    .build()

        private fun SpendsService.Error.toErrorServerResponse(
            countHeaderName: String,
            errorHeaderName: String,
            errorCodeHeaderName: String,
            loggingRepository: LoggingRepository,
            internationalizationRepository: InternationalizationRepository,
            languageCode: String? = null
        ) =
            when (this) {
                is SpendsService.Error.Business.NoSpendsFound ->
                    this.toErrorServerResponse(countHeaderName)

                is SpendsService.Error.Business.FutureSpend ->
                    this.toErrorServerResponse(
                        internationalizationRepository,
                        errorHeaderName,
                        errorCodeHeaderName,
                        languageCode
                    )

                is SpendsService.Error.Infrastructure ->
                    this.also { loggingRepository.error(it.cause) }
                        .infrastructureErrorToErrorServerResponse()
            }

        private fun SpendsService.Error.Business.NoSpendsFound.toErrorServerResponse(
            countHeaderName: String
        ) =
            ServerResponse
                .status(HttpStatus.NO_CONTENT)
                .header(countHeaderName, "0")
                .build()

        private fun SpendsService.Error.Business.FutureSpend.toErrorServerResponse(
            internationalizationRepository: InternationalizationRepository,
            errorHeaderName: String,
            errorCodeHeaderName: String,
            languageCode: String? = null
        ) =
            ServerResponse
                .status(HttpStatus.BAD_REQUEST)
                .header(
                    errorHeaderName,
                    internationalizationRepository.forCode(
                        this::class.qualifiedName!!,
                        languageCode
                    )
                )
                .header(errorCodeHeaderName, this::class.qualifiedName)
                .build()

        private fun SpendsService.Error.Infrastructure.infrastructureErrorToErrorServerResponse() =
            ServerResponse
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build()

        private fun Collection<Spend.Error>.toErrorServerResponse(
            internationalizationRepository: InternationalizationRepository,
            errorHeaderName: String,
            errorCodeHeaderName: String,
            languageCode: String? = null
        ) =
            ServerResponse
                .status(HttpStatus.BAD_REQUEST)
                .header(
                    errorHeaderName,
                    *this
                        .map {
                            it.toErrorMessageHeaderValues(
                                internationalizationRepository,
                                languageCode
                            )
                        }
                        .toTypedArray()
                )
                .header(
                    errorCodeHeaderName,
                    *this
                        .map { it.toErrorCodeHeaderValues() }
                        .toTypedArray()
                )
                .build()

        private fun Spend.Error.toErrorCodeHeaderValues() = this::class.qualifiedName

        private fun Spend.Error.toErrorMessageHeaderValues(
            internationalizationRepository: InternationalizationRepository,
            languageCode: String? = null
        ) =
            internationalizationRepository.forCode(
                this::class.qualifiedName!!,
                languageCode
            )

        private fun ServerRequest.toRequestLanguage(
            languageQueryParameterName: String
        ) =
            this.param(languageQueryParameterName)
                .getOrNull()
    }
}
