package io.github.tdavi156.listingwatch

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

/** The JSON body of every error response. `details` is left out of the JSON when empty. */
@Serializable
data class ErrorResponse(val error: String, val details: List<String> = emptyList())

/** Thrown when a request is well-formed JSON but breaks a rule, like a negative price. */
class ValidationException(val errors: List<String>) : RuntimeException("Validation failed: $errors")

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<ValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Validation failed", cause.errors))
        }
        // Ktor throws this when the body isn't valid JSON or doesn't match the request class.
        exception<BadRequestException> { call, _ ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Malformed request body"))
        }
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ErrorResponse(cause.message ?: "Not found"))
        }
        // Anything else is our bug. Log the details, but never send them to the client.
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled error", cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Internal server error"))
        }
    }
}
