package com.theo.plugins.openapi

import com.theo.plugins.common.*
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class OpenApiContractCodeGenTask @Inject constructor() : DefaultTask() {
    @TaskAction
    fun doWork() {
        try {
            var downloadProcesses = mutableListOf<Process>()
            var openApiContracts = mutableMapOf<String, String>()
            var dir = File(OPENAPI_CONTRACT_DIR)

            dir.listFiles()?.forEach{ file ->
                if (file.name.endsWith(YAML_EXT)) {
                    var fileName = file.name
                    println("Processing yaml file $fileName from directory")
                    openApiContracts[fileName.substringBefore("$YAML_EXT")] = "$OPENAPI_CONTRACT_DIR$fileName"
                }
            }

            openApiContracts.forEach {
                if (it.key.contains("$CLIENT_TAG-")) {
                    println("Generating Client Code: ${it.key}")
                    downloadProcesses.add(
                        buildCodeGenGradleCommand(it.value, CLIENT_TAG)
                    )
                } else {
                    println("Generating Server Code: ${it.key}")
                    downloadProcesses.add(
                        buildCodeGenGradleCommand(it.value, SERVER_TAG)
                    )
                }
            }

            while(downloadProcesses.any { x -> x.isAlive }) {
                Thread.sleep(RUNTIME_PROCESS_WAIT_TIME.toLong())
                // wait until download finishes
            }
        } catch (e: RuntimeException) {
            throw GradleException("Failed to generate OpenAPI contract code. ${e.message}", e)
        }
    }

    private fun buildCodeGenGradleCommand(url: String, type: String): Process {
        val command = "$GRADLEW_EXECUTABLE $BUILD_CONTRACT_CODE -Purl=$url -Ptype=$type"
        println("Executing gradle command to generate code based on openAPI contract: $command")
        return Runtime.getRuntime().exec("$command")
    }
}