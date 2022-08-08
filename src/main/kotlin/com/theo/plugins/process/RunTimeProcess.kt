package com.theo.plugins.process

class RunTimeProcess {
    fun runTimeExecute(command: String) {
        Runtime.getRuntime().exec(command)
    }
}