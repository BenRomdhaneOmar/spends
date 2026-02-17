package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.rest.router

import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.configuration.SpendsSettings
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.framework.FrameworkSettings
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.repository.InMemorySpendsRepository
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.rest.dto.PersistedSpendDTO
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.rest.dto.SpendDTO
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.rest.dto.toDateDTO
import kotlinx.datetime.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.returnResult
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@SpringBootTest
class SpendsRestRouterTest private constructor(
    @Autowired private val spendsSettings: SpendsSettings,
    @Autowired private val frameworkSettings: FrameworkSettings,
    @Autowired private val spendsRestRouter: SpendsRestRouter,
    @Autowired private val inMemorySpendsRepository: InMemorySpendsRepository
) {
    private val readRouterTestClient =
        RestTestClient
            .bindToRouterFunction(this.spendsRestRouter.ReadRoutes())
            .build()

    @BeforeEach
    fun setUp() {
        this.inMemorySpendsRepository
            .deleteAllSpends()
    }

    @AfterEach
    fun tearDown() {
        this.inMemorySpendsRepository
            .deleteAllSpends()
    }

    @Test
    fun `add spend must return error if spend is invalid`() {
        this.readRouterTestClient
            .post()
            .uri {
                it
                    .path("/spends")
                    .queryParam(this.spendsSettings.queries!!.language, this.frameworkSettings.defaultLanguageCode)
                    .build()
            }
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(
                SpendDTO(
                    0F.minus(Random.nextFloat()),
                    Clock.System.todayIn(TimeZone.currentSystemDefault()).minus(Random.nextInt(1,100), DateTimeUnit.DAY).toDateDTO()
                )
            )
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectHeader()
            .valueEquals(
                this.spendsSettings.headers!!.errors!!.codes,
                "com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.model.Spend.Error.ValueZeroOrNegative"
            )
            .expectHeader()
            .exists(this.spendsSettings.headers!!.errors!!.messages)
    }

    @Test
    fun `add spend must return error if spend is valid but date is invalid`() {
        this.readRouterTestClient
            .post()
            .uri {
                it
                    .path("/spends")
                    .queryParam(this.spendsSettings.queries!!.language, this.frameworkSettings.defaultLanguageCode)
                    .build()
            }
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(
                SpendDTO(
                    Random.nextFloat(),
                    Clock.System.todayIn(TimeZone.currentSystemDefault()).plus(Random.nextInt(1,100), DateTimeUnit.DAY).toDateDTO()
                )
            )
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectHeader()
            .valueEquals(
                this.spendsSettings.headers!!.errors!!.codes,
                "com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.service.SpendsService.Error.Business.FutureSpend"
            )
            .expectHeader()
            .exists(this.spendsSettings.headers!!.errors!!.messages)
    }

    @Test
    fun `add spend must return success if spend is valid and date is valid`() {
        val value = Random.nextFloat()
        val date = Clock.System.todayIn(TimeZone.currentSystemDefault()).minus(Random.nextInt(1,100), DateTimeUnit.DAY).toDateDTO()
        val description = Uuid.generateV7().toString()
        val spend =
            SpendDTO(
                value,
                date,
                description
            )

        this.readRouterTestClient
            .post()
            .uri {
                it
                    .path("/spends")
                    .queryParam(this.spendsSettings.queries!!.language, this.frameworkSettings.defaultLanguageCode)
                    .build()
            }
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(spend)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .doesNotExist(this.spendsSettings.headers!!.errors!!.codes)
            .expectHeader()
            .doesNotExist(this.spendsSettings.headers!!.errors!!.messages)
            .expectAll(
                { assertTrue { it.returnResult<PersistedSpendDTO>().responseBody!!.identifier.isNotBlank() } },
                { assertEquals(value, it.returnResult<PersistedSpendDTO>().responseBody!!.value) },
                { assertEquals(date, it.returnResult<PersistedSpendDTO>().responseBody!!.date) },
                { assertEquals(description, it.returnResult<PersistedSpendDTO>().responseBody!!.description) }
            )
    }

    @Test
    fun `all spends must return success if no spends are registered`() {
        this.readRouterTestClient
            .get()
            .uri {
                it
                    .path("/spends")
                    .queryParam(this.spendsSettings.queries!!.language, this.frameworkSettings.defaultLanguageCode)
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent
            .expectHeader()
            .doesNotExist(this.spendsSettings.headers!!.errors!!.codes)
            .expectHeader()
            .doesNotExist(this.spendsSettings.headers!!.errors!!.messages)
            .expectHeader()
            .valueEquals(this.spendsSettings.headers!!.count, 0)
            .expectBody()
            .isEmpty
    }

    @Test
    fun `all spends must return success if some spends are registered`() {
        val value1 = Random.nextFloat()
        val date1 = Clock.System.todayIn(TimeZone.currentSystemDefault()).minus(Random.nextInt(1,100), DateTimeUnit.DAY).toDateDTO()
        val description1 = Uuid.generateV7().toString()
        val spend1 =
            SpendDTO(
                value1,
                date1,
                description1
            )
        val value2 = Random.nextFloat()
        val date2 = Clock.System.todayIn(TimeZone.currentSystemDefault()).minus(Random.nextInt(1,100), DateTimeUnit.DAY).toDateDTO()
        val spend2 =
            SpendDTO(
                value2,
                date2
            )

        this.inMemorySpendsRepository
            .addSpend(
                spend1.toSpend()
                    .toMaybeSuccess()
                    .orNull()!!
            )

        this.inMemorySpendsRepository
            .addSpend(
                spend2.toSpend()
                    .toMaybeSuccess()
                    .orNull()!!
            )

        this.readRouterTestClient
            .get()
            .uri {
                it
                    .path("/spends")
                    .queryParam(this.spendsSettings.queries!!.language, this.frameworkSettings.defaultLanguageCode)
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .doesNotExist(this.spendsSettings.headers!!.errors!!.codes)
            .expectHeader()
            .doesNotExist(this.spendsSettings.headers!!.errors!!.messages)
            .expectHeader()
            .valueEquals(this.spendsSettings.headers!!.count, 2)
            .expectAll(
                { response -> assertTrue { response.returnResult<Collection<PersistedSpendDTO>>().responseBody!!.all { it.identifier.isNotBlank() } } },
                { response -> assertEquals(2, response.returnResult<Collection<PersistedSpendDTO>>().responseBody!!.size) },
                { response -> assertTrue { response.returnResult<Collection<PersistedSpendDTO>>().responseBody!!.any { it.value == value1 } } },
                { response -> assertTrue { response.returnResult<Collection<PersistedSpendDTO>>().responseBody!!.any { it.date == date1 } } },
                { response -> assertTrue { response.returnResult<Collection<PersistedSpendDTO>>().responseBody!!.any { it.description == description1 } } },
                { response -> assertTrue { response.returnResult<Collection<PersistedSpendDTO>>().responseBody!!.any { it.value == value2 } } },
                { response -> assertTrue { response.returnResult<Collection<PersistedSpendDTO>>().responseBody!!.any { it.date == date2 } } }
            )
    }
}