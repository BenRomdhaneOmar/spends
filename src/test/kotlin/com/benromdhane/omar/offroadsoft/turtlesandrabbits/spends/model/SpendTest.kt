package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.model

import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.random.Random
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class SpendTest {

    @Test
    fun `build function must return error if called with invalid value`() {
        val value = 0F
        val date = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val description = Uuid.generateV7().toString()

        val result =
            Spend
                .Builder
                .instance()
                .withValue(value)
                .withDate(date)
                .withDescription(description)
                .build()
                .toErrors()

        assertAll(
            { assertEquals(1, result.size) },
            { assertContains(result, Spend.Error.ValueZeroOrNegative) }
        )
    }

    @Test
    fun `build function must return error if called with invalid description`() {
        val value = Random(1).nextFloat()
        val date = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val description = Uuid.generateV7().toString().take(2)

        val result =
            Spend
                .Builder
                .instance()
                .withValue(value)
                .withDate(date)
                .withDescription(description)
                .build()
                .toErrors()

        assertAll(
            { assertEquals(1, result.size) },
            { assertContains(result, Spend.Error.DescriptionLengthLessThanThreeCharacters) }
        )
    }

    @Test
    fun `build function must return errors if called with multiple invalid elements`() {
        val value = Float.NaN
        val date = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val description = Uuid.generateV7().toString().take(2)

        val result =
            Spend
                .Builder
                .instance()
                .withValue(value)
                .withDate(date)
                .withDescription(description)
                .build()
                .toErrors()

        assertAll(
            { assertEquals(2, result.size) },
            { assertContains(result, Spend.Error.ValueZeroOrNegative) },
            { assertContains(result, Spend.Error.DescriptionLengthLessThanThreeCharacters) }
        )
    }

    @Test
    fun `build function must return spend if called with valid elements`() {
        val value = Random(1).nextFloat()
        val date = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val description = Uuid.generateV7().toString()

        val result =
            Spend
                .Builder
                .instance()
                .withValue(value)
                .withDate(date)
                .withDescription(description)
                .build()
                .toMaybeSuccess()
                .orNull()!!

        assertAll(
            { assertEquals(value, result.value) },
            { assertEquals(date, result.date) },
            { assertEquals(description, result.description.orNull()!!) }
        )
    }

    @Test
    fun `build function must return spend with empty description if called with valid elements including empty description string`() {
        val value = Random(1).nextFloat()
        val date = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val description = ""

        val result =
            Spend
                .Builder
                .instance()
                .withValue(value)
                .withDate(date)
                .withDescription(description)
                .build()
                .toMaybeSuccess()
                .orNull()!!

        assertAll(
            { assertEquals(value, result.value) },
            { assertEquals(date, result.date) },
            { assertTrue { result.description.empty() } }
        )
    }

    @Test
    fun `build function must return spend with empty description if called with valid elements including null description`() {
        val value = Random(1).nextFloat()
        val date = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val description: String? = null

        val result =
            Spend
                .Builder
                .instance()
                .withValue(value)
                .withDate(date)
                .withDescription(description)
                .build()
                .toMaybeSuccess()
                .orNull()!!

        assertAll(
            { assertEquals(value, result.value) },
            { assertEquals(date, result.date) },
            { assertTrue { result.description.empty() } }
        )
    }
}