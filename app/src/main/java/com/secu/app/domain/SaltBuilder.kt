package com.secu.app.domain

import com.secu.app.data.ServiceNormalizer

object SaltBuilder {

    private const val VERSION_TAG = "v1.3.0"

    fun build(serviceName: String, deviceComponent: String = "", rotationVersion: Int = 1): String {
        val normalizedService = ServiceNormalizer.normalize(serviceName)
        require(normalizedService.isNotEmpty()) { "Service name must not be blank" }
        return "VERSION_TAG:deviceComponent:normalizedService:rotationVersion"
    }
}
