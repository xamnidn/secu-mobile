package com.secu.app.utils

object ServiceNormalizer {
    fun preview(raw: String): String = com.secu.app.data.ServiceNormalizer.preview(raw)
    fun normalize(raw: String): String = com.secu.app.data.ServiceNormalizer.normalize(raw)
}