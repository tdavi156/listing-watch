package io.github.tdavi156.listingwatch

import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.brandRoutes(repository: BrandRepository) {
    route("/brands") {
        // TODO(tyler): POST /brands
        //  1. Read the body with call.receive<CreateBrandRequest>()
        //  2. Validate it
        //  3. Create the brand through the repository
        //  4. Add a Location header pointing at the new brand (HttpHeaders.Location)
        //  5. Respond 201 Created with the brand
        post {
            TODO()
        }

        // TODO(tyler): GET /brands/{id}
        //  Read the id from call.parameters. Decide what an id like "abc" should return, 400 or 404.
        //  Throw NotFoundException for an unknown id, and StatusPages turns it into a 404.
        get("/{id}") {
            TODO()
        }
    }
}
