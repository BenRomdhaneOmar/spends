package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.architectur

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import org.junit.jupiter.api.Test

class BoundariesTest {
    private val classFileImporter =
        ClassFileImporter()
            .importPackages("com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends")

    @Test
    fun `service classes must have service as a part of the name`() {
        ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("..service..")
            .and()
            .haveSimpleNameNotContaining("Test")
            .and()
            .areTopLevelClasses()
            .should()
            .haveSimpleNameContaining("Service")
            .because("service must be always in service package")
            .check(this.classFileImporter)
    }

    @Test
    fun `service classes must depend only on helpers and language apis`() {
        ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("..service..")
            .and()
            .haveSimpleNameNotContaining("Test")
            .should()
            .onlyDependOnClassesThat()
            .areInterfaces()
            .orShould()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(
                "java..",
                "kotlin..",
                "kotlinx..",
                "org.jetbrains..",
                "com.benromdhane.omar.offroadsoft..",
                "com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.model.."
            )
            .because("service must be always io and framework independent")
            .check(this.classFileImporter)
    }

    @Test
    fun `model classes must depend only on helpers and language apis`() {
        ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("..model..")
            .and()
            .haveSimpleNameNotContaining("Test")
            .should()
            .onlyDependOnClassesThat()
            .areInterfaces()
            .orShould()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(
                "java..",
                "kotlin..",
                "kotlinx..",
                "org.jetbrains..",
                "com.benromdhane.omar.offroadsoft..",
                "com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.model.."
            )
            .because("models must be always io and framework independent")
            .check(this.classFileImporter)
    }
//    TODO(check no classes inside service except for java and kotlin and kotlinx and helper)
//    TODO(check no classes inside repository interface except for java and kotlin and kotlinx and helper)
//    TODO(check no classes inside models except for java and kotlin and kotlinx and helper)
//    TODO(check repository interface is used only from service)
//    TODO(check repository implementation is instantiated only from configuration file)
//    TODO(check service is used only from handler)
//    TODO(check service is instantiated only from configuration file)
//    TODO(check handler is used only from router)
//    TODO(check handler is instantiated only from configuration file)
}