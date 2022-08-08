package com.theo.plugins.openapi

import com.theo.plugins.common.*
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class OpenApiContractDownloadTask @Inject constructor() : DefaultTask() {
    @Input
    lateinit var producers: ListProperty<String>

    @Input
    lateinit var consumers: ListProperty<String>

    @TaskAction
    fun doWork() {
        val runningProcesses = mutableListOf<Process>()

        try {
            producers.get().forEach {
                var process = buildDownloadGradleCommand(it, SERVER_TAG)
                runningProcesses.add(process)
            }

            consumers.get().forEach {
                var process = buildDownloadGradleCommand(it, CLIENT_TAG)
                runningProcesses.add(process)
            }

            while(runningProcesses.any { x -> x.isAlive }) {
                Thread.sleep(RUNTIME_PROCESS_WAIT_TIME.toLong())
                // wait until download finishes
            }
        } catch (e: RuntimeException) {
            throw GradleException("Failed to download OpenAPI contract. ${e.message}", e)
        }
    }

    private fun buildDownloadGradleCommand(openApi: String, type: String): Process {
        val url = buildUrl(openApi)
        println("Downloading $type OpenAPI from url: $url")
        val command = "$GRADLEW_EXECUTABLE $DOWNLOAD_OPENAPI_CONTRACT -Purl=$url -Ptype=$type"
        println("Executing gradle command to download openAPI contract: $command")
        return Runtime.getRuntime().exec("$command")
    }

    private fun buildUrl(url: String): String {
        var urlParts = url.split(":")
        var boundedContext = urlParts[0]
        var apiName = urlParts[1]
        var majorVersion = urlParts[2].split(".")[0]

        return "$OPENAPI_CONTRACT_URL_BASE$boundedContext/OpenAPI/$apiName/v$majorVersion$YAML_EXT"
    }
}