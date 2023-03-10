package com.mapbox.navigation.instrumentation_tests.utils

import java.net.URL

fun URL.parameters(): Map<String, String> {
    return query.split('&').associate {
        val parts = it.split('=')
        val name = parts.firstOrNull() ?: ""
        val value = parts.drop(1).firstOrNull() ?: ""
        name to value
    }
}