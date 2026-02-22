package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.service

import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.model.Spend
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.infrastructure.repository.InMemorySpendsRepository
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@SpringBootTest
class SpendsServiceTest private constructor(
    @Autowired private val spendsService: SpendsService,
    @Autowired private val inMemorySpendsRepository: InMemorySpendsRepository
) {

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
    fun `add spend function must return error if spend date is in the future`() {
        val date =
            Clock.System.todayIn(TimeZone.currentSystemDefault())
                .plus(1, DateTimeUnit.DAY)
        val spend =
            Spend
                .Builder
                .instance()
                .withValue(Random(1).nextFloat())
                .withDate(date)
                .withDescription(Uuid.generateV7().toString())
                .build()
                .toMaybeSuccess()
                .orNull()!!

        val result =
            this.spendsService
                .addSpend(
                    spend
                )
                .toMaybeError()
                .orNull()!!

        assertEquals(SpendsService.Error.Business.FutureSpend, result)
    }

    @Test
    fun `add spend function must return persistent spend if spend is valid`() {
        val value = Random(1).nextFloat()
        val date = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val description = Uuid.generateV7().toString()

        val spend =
            Spend
                .Builder
                .instance()
                .withValue(value)
                .withDate(date)
                .withDescription(description)
                .build()
                .toMaybeSuccess()
                .orNull()!!

        val result =
            this.spendsService
                .addSpend(
                    spend
                )
                .toMaybeSuccess()
                .orNull()!!

        assertAll(
            { assertEquals(value, result.value) },
            { assertEquals(date, result.date) },
            { assertEquals(description, result.description.orNull()!!) },
            { assertThat(result.identifier).isNotBlank }
        )
    }

    @Test
    fun `all spends function must return error if no spends are added`() {
        val result =
            this.spendsService
                .allSpends()
                .toMaybeError()
                .orNull()!!

        assertEquals(SpendsService.Error.Business.NoSpendsFound, result)
    }

    @Test
    fun `all spends function must return persisted spends if spends already added`() {
        val spend1 =
            Spend
                .Builder
                .instance()
                .withValue(Random(1).nextFloat())
                .withDate(Clock.System.todayIn(TimeZone.currentSystemDefault()))
                .withDescription(Uuid.generateV7().toString())
                .build()
                .toMaybeSuccess()
                .orNull()!!
        val spend2 =
            Spend
                .Builder
                .instance()
                .withValue(Random(1).nextFloat())
                .withDate(Clock.System.todayIn(TimeZone.currentSystemDefault()))
                .withDescription(Uuid.generateV7().toString())
                .build()
                .toMaybeSuccess()
                .orNull()!!
        this.inMemorySpendsRepository
            .addSpend(
                spend1
            )
        this.inMemorySpendsRepository
            .addSpend(
                spend2
            )
        val result =
            this.spendsService
                .allSpends()
                .toMaybeSuccess()
                .orNull()!!

        assertAll(
            { assertEquals(2, result.size) },
            { assertTrue { result.any { it.value.equals(spend1.value) } } },
            { assertTrue { result.any { it.date == spend1.date } } },
            { assertTrue { result.any { it.description == spend1.description } } },
            { assertTrue { result.any { it.value.equals(spend2.value) } } },
            { assertTrue { result.any { it.date == spend2.date } } },
            { assertTrue { result.any { it.description == spend2.description } } }
        )
    }
}