package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.repository

import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.model.Spend
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.service.SpendsService
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.infrastructure.repository.InMemorySpendsRepository
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@SpringBootTest
class SpendsRepositoryTest private constructor(
    @Autowired private val spendsRepository: SpendsRepository,
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
    fun `add spend must persist spend and assign identifier for it`() {
        val spend =
            Spend.Builder
                .instance()
                .withValue(Random.nextFloat())
                .withDate(
                    Clock.System.todayIn(TimeZone.currentSystemDefault())
                        .plus(Random(1).nextInt(), DateTimeUnit.DAY)
                )
                .withDescription(Uuid.generateV7().toString())
                .build()
                .toMaybeSuccess()
                .orNull()!!

        val result =
            this.spendsRepository
                .addSpend(
                    spend
                )
                .toMaybeSuccess()
                .orNull()!!

        assertAll(
            { assertTrue { result.identifier.isNotBlank() } },
            { assertEquals(spend.value, result.value) },
            { assertEquals(spend.date, result.date) },
            { assertEquals(spend.description, result.description) },
            { assertEquals(1, this.inMemorySpendsRepository.count()) }
        )
    }

    @Test
    fun `add spend must persist spend multiple time without error`() {
        val spend =
            Spend.Builder
                .instance()
                .withValue(Random.nextFloat())
                .withDate(
                    Clock.System.todayIn(TimeZone.currentSystemDefault())
                        .plus(Random(1).nextInt(), DateTimeUnit.DAY)
                )
                .withDescription(Uuid.generateV7().toString())
                .build()
                .toMaybeSuccess()
                .orNull()!!

        val persistedSpend =
            this.spendsRepository
                .addSpend(
                    spend
                )
                .toMaybeSuccess()
                .orNull()!!

        val result =
            this.spendsRepository
                .addSpend(
                    spend
                )
                .toMaybeSuccess()
                .orNull()!!

        assertAll(
            { assertNotEquals(persistedSpend.identifier, result.identifier) },
            { assertEquals(spend.value, result.value) },
            { assertEquals(spend.date, result.date) },
            { assertEquals(spend.description, result.description) },
            { assertEquals(2, this.inMemorySpendsRepository.count()) }
        )
    }

    @Test
    fun `all spend must return error if not spends are saved`() {
        val result =
            this.spendsRepository
                .allSpends()
                .toMaybeError()
                .orNull()!!

        assertEquals(SpendsService.Error.Business.NoSpendsFound, result)
    }

    @Test
    fun `all spend must return persisted spend`() {
        val spend =
            Spend.Builder
                .instance()
                .withValue(Random.nextFloat())
                .withDate(
                    Clock.System.todayIn(TimeZone.currentSystemDefault())
                        .plus(Random(1).nextInt(), DateTimeUnit.DAY)
                )
                .withDescription(Uuid.generateV7().toString())
                .build()
                .toMaybeSuccess()
                .orNull()!!
        val persistedSpend =
            this.spendsRepository
                .addSpend(
                    spend
                )
                .toMaybeSuccess()
                .orNull()!!

        val result =
            this.spendsRepository
                .allSpends()
                .toMaybeSuccess()
                .orNull()!!

        assertAll(
            { assertEquals(persistedSpend.identifier, result.first().identifier) },
            { assertEquals(spend.value, result.first().value) },
            { assertEquals(spend.date, result.first().date) },
            { assertEquals(spend.description, result.first().description) },
            { assertEquals(1, result.size) }
        )
    }

    @Test
    fun `all spend must return persisted spends`() {
        val spend =
            Spend.Builder
                .instance()
                .withValue(Random.nextFloat())
                .withDate(
                    Clock.System.todayIn(TimeZone.currentSystemDefault())
                        .plus(Random(1).nextInt(), DateTimeUnit.DAY)
                )
                .withDescription(Uuid.generateV7().toString())
                .build()
                .toMaybeSuccess()
                .orNull()!!
        val firstPersistedSpend =
            this.spendsRepository
                .addSpend(
                    spend
                )
                .toMaybeSuccess()
                .orNull()!!
        val secondPersistedSpend =
            this.spendsRepository
                .addSpend(
                    spend
                )
                .toMaybeSuccess()
                .orNull()!!

        val result =
            this.spendsRepository
                .allSpends()
                .toMaybeSuccess()
                .orNull()!!

        assertAll(
            { assertEquals(firstPersistedSpend.identifier, result.first().identifier) },
            { assertEquals(firstPersistedSpend.value, result.first().value) },
            { assertEquals(firstPersistedSpend.date, result.first().date) },
            { assertEquals(firstPersistedSpend.description, result.first().description) },
            { assertEquals(secondPersistedSpend.identifier, result.last().identifier) },
            { assertEquals(secondPersistedSpend.value, result.last().value) },
            { assertEquals(secondPersistedSpend.date, result.last().date) },
            { assertEquals(secondPersistedSpend.description, result.last().description) },
            { assertEquals(2, result.size) }
        )
    }
}