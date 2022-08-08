package com.theo.plugins.openapi

import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

class OpenApiPluginTest {
    @Test
    fun `downloadContract Task is included in plugin`() {
        val project: Project = ProjectBuilder.builder().build()
        project.getPluginManager().apply("com.theo.openApiPlugin")
        assertTrue(project.getTasks().getByName("downloadContract") is OpenApiContractDownloadTask)
    }

    @Test
    fun `CodeGen Task is included in plugin`() {
        val project: Project = ProjectBuilder.builder().build()
        project.getPluginManager().apply("com.theo.openApiPlugin")
        assertTrue(project.getTasks().getByName("generateContract") is OpenApiContractCodeGenTask)
    }

    @Test
    fun `buildKotlinContractCode is included in plugin`() {
        val project: Project = ProjectBuilder.builder().build()
        project.getPluginManager().apply("com.theo.openApiPlugin")
        assertTrue(project.getTasks().getByName("buildKotlinContractCode") is GenerateTask)
    }

    @Test
    fun `downloadOpenApiContract is included in plugin`() {
        val project: Project = ProjectBuilder.builder().build()
        project.getPluginManager().apply("com.theo.openApiPlugin")
        assertTrue(project.getTasks().getByName("downloadOpenApiContract") is Download)
    }
}