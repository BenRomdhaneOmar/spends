package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.architectur

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import org.junit.jupiter.api.Test

class BoundariesTest {
    private val classFileImporter =
        ClassFileImporter()
            .importPackages("com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends")

    @Test
    fun `core classes must be always io and framework independent`() {
        ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core..")
            .and()
            .haveSimpleNameNotContaining("Test")
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(
                "java..",
                "kotlin..",
                "kotlinx..",
                "org.jetbrains..",
                "com.benromdhane.omar.offroadsoft..",
                "com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core"
            )
            .because("core components must be always io and framework independent")
            .check(this.classFileImporter)
    }

    @Test
    fun `core repository must be always an abstraction contract`() {
        ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.repository")
            .and()
            .haveSimpleNameNotContaining("Test")
            .should()
            .beInterfaces()
            .because("core repository must be always an abstraction contract")
            .check(this.classFileImporter)
    }
}