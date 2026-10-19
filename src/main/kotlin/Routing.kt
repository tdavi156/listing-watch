package io.github.tdavi156.listingwatch

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    // Each app instance gets its own store, so every test starts empty.
    val brandRepository: BrandRepository = InMemoryBrandRepository()

    routing {
        get("/health") {
            call.respondText("OK")
        }

        brandRoutes(brandRepository)
    }
}