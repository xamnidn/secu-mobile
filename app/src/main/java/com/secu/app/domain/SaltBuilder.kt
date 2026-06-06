package com.secu.app.domain

import com.secu.app.data.ServiceNormalizer

object SaltBuilder {

    // Single source of truth – nanti akan diambil dari BuildConfig setelah build pertama
    private const val VERSION_TAG = "v1.3.0"

    fun build(serviceName: String, deviceComponent: String = ""): String {
        val normalizedService = ServiceNormalizer.normalize(serviceName)
        require(normalizedService.isNotEmpty()) { "Service name must not be blank" }
        return "$VERSION_TAG:$deviceComponent:$normalizedService:1"
    }
}