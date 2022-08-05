package com.theo.plugins.openapi

import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import java.io.File

class OpenApiPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create<OpenApiPluginExtension>("openApiPluginExtension")

        project.pluginManager.apply("org.openapi.generator")
        project.pluginManager.apply("de.undercouch.download")

        project.tasks.register("buildKotlinContractCode", GenerateTask::class) {
            val yamlFilePath = project.properties["url"].toString()
            val typeOfGenerator = project.properties["type"].toString()
            val parsedGeneratorName = if (typeOfGenerator == "client") "kotlin" else "kotlin-spring"
            if (yamlFilePath.isNotEmpty()) {
                generatorName.set(parsedGeneratorName)
                inputSpec.set("$yamlFilePath")
                outputDir.set("${project.buildDir}/kotlin")
                apiPackage.set("org.openapitools.ikigai.api")
                configOptions.put("interfaceOnly", "true")
                configOptions.put("serializationLibrary", "gson")
            }
        }

        project.tasks.register("downloadOpenApiContract", Download::class) {
            var downloadFileUrl = project.properties["url"].toString()
            var downloadOpenApiType = project.properties["type"].toString()

            if (downloadFileUrl.isNotEmpty()) {
                var urlParts = downloadFileUrl.split("/")
                if (urlParts.size > 2) {
                    var fileName = urlParts[urlParts.size - 2]
                    this.src(downloadFileUrl)
                    if (downloadOpenApiType == "client") {
                        this.dest(File("src/main/resources/static/", "client-$fileName.yaml"))
                    } else {
                        this.dest(File("src/main/resources/static/", "server-$fileName.yaml"))
                    }
                }
            }
        }

        project.tasks.register<OpenApiContractDownloadTask>("downloadContract") {
            producers = extension.producers
            consumers = extension.consumers
        }

        project.tasks.register<OpenApiContractCodeGenTask>("generateContract") {
            dependsOn("downloadContract")
            outputDir = "${project.buildDir}/kotlin"
        }
    }
}