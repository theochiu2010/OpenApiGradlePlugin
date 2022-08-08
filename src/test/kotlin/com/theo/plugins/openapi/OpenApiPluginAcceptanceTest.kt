package com.theo.plugins.openapi

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.File

@RunWith(PowerMockRunner::class)
@PrepareForTest(Runtime::class)
class OpenApiPluginAcceptanceTest {

    @Rule
    var testProjectDir = TemporaryFolder()

    private lateinit var buildFile: File
    private lateinit var gradleRunner: GradleRunner

    private var mockRuntime: Runtime = mock(Runtime::class.java)
    private var mockProcess: Process = mock(Process::class.java)

    @Before
    fun setup() {
        buildFile = testProjectDir.newFile("build.gradle.kts")
        buildFile.appendText("""
            plugins {
                id("com.theo.openApiPlugin")
                id("org.openapi.generator") version "5.1.1"
                id("de.undercouch.download") version "5.1.0"
            }
            
        """.trimIndent())

        gradleRunner = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(testProjectDir.root)
            .withTestKitDir(testProjectDir.newFolder())
    }

    @Test
    fun `can execute generateContract`() {
        PowerMockito.mockStatic(Runtime::class.java)
        `when`(Runtime.getRuntime()).thenReturn(mockRuntime)
        `when`(mockRuntime.exec(any<String>())).thenReturn(mockProcess)
        `when`(mockProcess.isAlive).thenReturn(false)

        val result = gradleRunner
            .withArguments("generateContract")
            .build()

        assertThat(result.task(":generateContract")!!.outcome.name).isEqualTo("SUCCESS")
    }

    @Test
    fun `can execute downloadContract`() {
        PowerMockito.mockStatic(Runtime::class.java)
        `when`(Runtime.getRuntime()).thenReturn(mockRuntime)
        `when`(mockRuntime.exec(any<String>())).thenReturn(mockProcess)
        `when`(mockProcess.isAlive).thenReturn(false)

        buildFile.appendText("""
            configure<com.theo.plugins.openapi.OpenApiPluginExtension> {
                producers.set(listOf(
                    "planet:planetAPI:1.0.0",
                    "inventory:inventoryAPI:1.0.0"
                ))
                consumers.set(listOf(
                    "nrs:nrsAPI:1.0.0",
                    "pet:petAPI:1.0.0"
                ))
            }
        """.trimIndent())

        val result = gradleRunner
            .withArguments("downloadContract")
            .build()

        assertThat(result.task(":downloadContract")!!.outcome.name).isEqualTo("SUCCESS")
    }
}