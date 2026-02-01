package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.model

import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.random.Random
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class PersistedSpendTest {

    @Test
    fun `of function must return error if called with invalid identifier`() {
        val identifier = ""
        val spend =
            Spend
                .Builder
                .instance()
                .withValue(Random(1).nextFloat())
                .withDate(Clock.System.todayIn(TimeZone.currentSystemDefault()))
                .withDescription(Uuid.generateV7().toString())
                .build()
                .toMaybeSuccess()
                .orNull()!!

        val result =
            PersistedSpend
                .of(
                    identifier,
                    spend
                )
                .toErrors()

        assertAll(
            { assertEquals(1, result.size) },
            { assertContains(result, PersistedSpend.Error.IdentifierEmpty) }
        )
    }

    @Test
    fun `of function must return persisted spend if called with valid elements`() {
        val identifier = Uuid.generateV7().toString()
        val spend =
            Spend
                .Builder
                .instance()
                .withValue(Random(1).nextFloat())
                .withDate(Clock.System.todayIn(TimeZone.currentSystemDefault()))
                .withDescription(Uuid.generateV7().toString())
                .build()
                .toMaybeSuccess()
                .orNull()!!

        val result =
            PersistedSpend
                .of(
                    identifier,
                    spend
                )
                .toMaybeSuccess()
                .orNull()!!

        assertAll(
            { assertEquals(identifier, result.identifier) },
            { assertEquals(spend.value, result.value) },
            { assertEquals(spend.date, result.date) },
            { assertEquals(spend.description, result.description) }
        )
    }
}