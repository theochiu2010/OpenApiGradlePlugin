package com.theo.plugins.openapi

import org.gradle.api.provider.ListProperty

interface OpenApiPluginExtension {
    val producers: ListProperty<String>
    val consumers: ListProperty<String>
}