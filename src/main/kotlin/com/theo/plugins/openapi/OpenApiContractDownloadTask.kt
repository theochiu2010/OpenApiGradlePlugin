package com.theo.plugins.openapi

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class OpenApiContractDownloadTask @Inject constructor() : DefaultTask() {
    @Input
    lateinit var producers: ListProperty<String>

    @Input
    lateinit var consumers: ListProperty<String>

    private val GRADLEW_EXECUTABLE = "./gradlew"
    private val DOWNLOAD_OPENAPI_CONTRACT = "downloadOpenApiContract"
    private val PARAMETER = "-P"
    private val ROOT = "https://raw.githubusercontent.com/theochiu2010/OpenApiContracts/main/contracts/"

    @TaskAction
    fun doWork() {
        var downloadProcesses = mutableListOf<Process>()

        try {
            producers.get().forEach {
                val url = constructUrl(it)
                println("Downloading producer OpenAPI from url: $url")
                val command = "$GRADLEW_EXECUTABLE $DOWNLOAD_OPENAPI_CONTRACT ${PARAMETER}url=$url ${PARAMETER}type=server"

                var process = Runtime.getRuntime().exec("$command")
                downloadProcesses.add(process)
            }

            consumers.get().forEach {
                val url = constructUrl(it)
                println("Downloading consumer OpenAPI from url: $url")
                val command = "$GRADLEW_EXECUTABLE $DOWNLOAD_OPENAPI_CONTRACT ${PARAMETER}url=$url ${PARAMETER}type=client"

                var process = Runtime.getRuntime().exec("$command")
                downloadProcesses.add(process)
            }

            while(downloadProcesses.any { x -> x.isAlive }) {
                Thread.sleep(100)
                // wait until download finishes
            }
        } catch (e: RuntimeException) {
            throw GradleException("Failed to download OpenAPI contract. ", e)
        }
    }

    private fun constructUrl(url: String): String {
        var urlParts = url.split(":")
        var boundedContext = urlParts[0]
        var apiName = urlParts[1]
        var majorVersion = urlParts[2].split(".")[0]

        return "$ROOT$boundedContext/OpenAPI/$apiName/v$majorVersion.yaml"
    }
}