package io.github.tdavi156.listingwatch

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        // The plain Json instance is strict. It rejects unknown fields and unquoted strings.
        // Ktor's own default is lenient, which hides client mistakes.
        json(Json)
    }
}
