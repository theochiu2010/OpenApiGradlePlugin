package com.theo.plugins.openapi

import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class OpenApiPluginIntegrationTest extends Specification {
    @TempDir
    File testProjectDir
    File buildFile

    def setup() {
        buildFile = new File(testProjectDir, 'build.gradle.kts')
        buildFile << """
            plugins {
                id("com.theo.openApiPlugin")
            }
        """
    }

    def "can successfully run generateContract Task in openApiPlugin"() {
        buildFile << """
            configure<OpenApiPluginExtension> {
                producers.set(listOf(
                    "planet:planetAPI:1.0.0",
                    "inventory:inventoryAPI:1.0.0"
                ))
                consumers.set(listOf(
                    "nrs:nrsAPI:1.0.0",
                    "pet:petAPI:1.0.0"
                ))
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withArguments('generateContract')
                .withPluginClasspath()
                .build()

        then:
        result.task(":generateContract").outcome == SUCCESS
    }
}