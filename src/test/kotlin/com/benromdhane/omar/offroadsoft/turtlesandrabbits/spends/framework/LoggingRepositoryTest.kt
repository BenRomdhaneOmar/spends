package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.framework

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class LoggingRepositoryTest {

    class A
    class B

    @Test
    fun `from must return same instance for same logged class`() {
        val result1 =
            LoggingRepository
                .from(A::class)
        val result2 =
            LoggingRepository
                .from(A::class)

        assertEquals(result1, result2)
    }

    @Test
    fun `from must return different instances for different logged class`() {
        val resultA =
            LoggingRepository
                .from(A::class)
        val resultB =
            LoggingRepository
                .from(B::class)

        assertNotEquals(resultA, resultB)
    }

    @Test
    fun `from must not throw any exception if trace function called`() {
        val logger =
            LoggingRepository
                .from(A::class)

        assertDoesNotThrow { logger.trace(Uuid.generateV7().toString()) }
    }

    @Test
    fun `from must not throw any exception if debug function called`() {
        val logger =
            LoggingRepository
                .from(A::class)

        assertDoesNotThrow { logger.debug(Uuid.generateV7().toString()) }
    }

    @Test
    fun `from must not throw any exception if info function called`() {
        val logger =
            LoggingRepository
                .from(A::class)

        assertDoesNotThrow { logger.info(Uuid.generateV7().toString()) }
    }

    @Test
    fun `from must not throw any exception if warning function called`() {
        val logger =
            LoggingRepository
                .from(A::class)

        assertDoesNotThrow { logger.warning(Uuid.generateV7().toString()) }
    }

    @Test
    fun `from must not throw any exception if error with throwable function called`() {
        val logger =
            LoggingRepository
                .from(A::class)

        assertDoesNotThrow { logger.error(Exception(Uuid.generateV7().toString())) }
    }

    @Test
    fun `from must not throw any exception if error with throwable and message function called`() {
        val logger =
            LoggingRepository
                .from(A::class)

        assertDoesNotThrow {
            logger.error(
                Uuid.generateV7().toString(),
                Exception(Uuid.generateV7().toString())
            )
        }
    }
}