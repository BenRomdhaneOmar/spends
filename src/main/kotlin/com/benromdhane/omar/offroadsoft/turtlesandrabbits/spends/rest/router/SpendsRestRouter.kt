package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.rest.router

import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.rest.dto.PersistedSpendDTO
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.rest.dto.SpendDTO
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.rest.handler.SpendsRestHandler
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.function.router

@Configuration(proxyBeanMethods = false)
@RegisterReflectionForBinding(
    SpendDTO::class,
    PersistedSpendDTO::class
)
class SpendsRestRouter private constructor(
    private val spendsRestHandler: SpendsRestHandler
) {

    @RouterOperations(
        RouterOperation(
            path = "/spends",
            method = [RequestMethod.GET],
            produces = [MediaType.APPLICATION_JSON_VALUE],
            operation = Operation(
                operationId = "fetchAllSpends",
                parameters = [
                    Parameter(
                        name = "language",
                        `in` = ParameterIn.QUERY,
                        required = false
                    )
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = [
                            Content(
                                schema =
                                    Schema(
                                        implementation = Array<PersistedSpendDTO>::class
                                    )
                            )
                        ]
                    ),
                    ApiResponse(
                        responseCode = "201",
                        description = "No content",
                        headers = [
                            Header(
                                name = "spends_count"
                            )
                        ]
                    ),
                    ApiResponse(
                        responseCode = "500",
                        description = "internal server error"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/spends",
            method = [RequestMethod.POST],
            consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE],
            operation = Operation(
                operationId = "addSpend",
                parameters = [
                    Parameter(
                        name = "language",
                        `in` = ParameterIn.QUERY,
                        required = false
                    )
                ],
                requestBody = RequestBody(
                    required = true,
                    content = [
                        Content(
                            schema = Schema(
                                implementation = SpendDTO::class
                            )
                        )
                    ]
                ),
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = [
                            Content(
                                schema =
                                    Schema(
                                        implementation = PersistedSpendDTO::class
                                    )
                            )
                        ]
                    ),
                    ApiResponse(
                        responseCode = "400",
                        description = "bad request",
                        headers = [
                            Header(
                                name = "errors_messages"
                            ),
                            Header(
                                name = "errors_codes"
                            )
                        ]
                    ),
                    ApiResponse(
                        responseCode = "500",
                        description = "internal server error"
                    )
                ]
            )
        )
    )
    @Bean
    fun ReadRoutes() =
        router {
            "/spends"
                .nest {
                    GET("", spendsRestHandler::fetchAllSpends)
                    POST("", spendsRestHandler::addSpend)
                }
        }
}