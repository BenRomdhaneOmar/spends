package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.framework

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@SpringBootTest
class InternationalizationRepositoryTest(
    @Autowired private val internationalizationRepository: InternationalizationRepository,
    @Autowired private val frameworkSettings: FrameworkSettings
) {

    @Test
    fun `for code must return default message code if language code is provided but does not present a valid language`() {
        val messageCode = Uuid.generateV7().toString()
        val languageCode = Uuid.generateV7().toString()

        val result =
            this.internationalizationRepository
                .forCode(
                    messageCode,
                    languageCode
                )

        assertEquals(messageCode, result)
    }

    @Test
    fun `for code must return default message code if message code provided does not exist in bundles`() {
        val messageCode = Uuid.generateV7().toString()
        val languageCode = Locale.getDefault().language

        val result =
            this.internationalizationRepository
                .forCode(
                    messageCode,
                    languageCode
                )

        assertEquals(messageCode, result)
    }

    @Test
    fun `for code must return internationalized message code if message code provided exist in bundles`() {
        val messageCode = "test.message"
        val languageCode = "en"

        val result =
            this.internationalizationRepository
                .forCode(
                    messageCode,
                    languageCode
                )

        assertEquals("${messageCode}_$languageCode", result)
    }

    @Test
    fun `for code must return internationalized message code if message code provided exist in bundles using default language`() {
        val messageCode = "test.message"

        val result =
            this.internationalizationRepository
                .forCode(
                    messageCode
                )

        assertEquals("${messageCode}_${this.frameworkSettings.defaultLanguageCode}", result)
    }

    @Test
    fun `for code must return default message code if message code provided exist in bundles`() {
        val messageCode = "test.message"
        val languageCode = "ar"

        val result =
            this.internationalizationRepository
                .forCode(
                    messageCode,
                    languageCode
                )

        assertEquals(messageCode, result)
    }
}