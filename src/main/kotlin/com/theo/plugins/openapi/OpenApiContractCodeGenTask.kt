package com.theo.plugins.openapi

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class OpenApiContractCodeGenTask @Inject constructor() : DefaultTask() {
    @Input
    lateinit var outputDir: String

    private val GRADLEW_EXECUTABLE = "./gradlew"
    private val BUILD_CONTRACT_CODE = "buildKotlinContractCode"
    private val PARAMETER = "-P"

    @TaskAction
    fun doWork() {
        try {
            var downloadProcesses = mutableListOf<Process>()
            var openApiContracts = mutableMapOf<String, String>()
            var dir = File("src/main/resources/static/")

            dir.listFiles().forEach{ file ->
                if (file.name.endsWith(".yaml")) {
                    var fileName = file.name
                    println("Processing file: $fileName from directory")
                    openApiContracts[fileName.substringBefore(".yaml")] = "src/main/resources/static/$fileName"
                }
            }

            println("Captured open api contracts: $openApiContracts")

            openApiContracts.forEach {
                if (it.key.contains("client-")) {
                    println("Generating Client Code: ${it.key}")
                    val genCodeCommand = "$GRADLEW_EXECUTABLE $BUILD_CONTRACT_CODE ${PARAMETER}url=${it.value} ${PARAMETER}type=client"

                    val process = Runtime.getRuntime().exec("$genCodeCommand")
                    downloadProcesses.add(process)
                } else {
                    println("Generating Server Code: ${it.key}")
                    val genCodeCommand = "$GRADLEW_EXECUTABLE $BUILD_CONTRACT_CODE ${PARAMETER}url=${it.value} ${PARAMETER}type=server"

                    val process = Runtime.getRuntime().exec("$genCodeCommand")
                    downloadProcesses.add(process)
                }
            }

            while(downloadProcesses.any { x -> x.isAlive }) {
                Thread.sleep(250)
                // wait until download finishes
            }
        } catch (e: RuntimeException) {
            throw GradleException("Failed to generate OpenAPI contract code. ", e)
        }
    }
}