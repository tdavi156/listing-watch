package io.github.tdavi156.listingwatch

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder

class BrandRoutesTest {

    /** A test client that sends and reads JSON. Call it inside testApplication { }. */
    private fun ApplicationTestBuilder.jsonClient(): HttpClient = createClient {
        install(ContentNegotiation) { json() }
    }

    // TODO(tyler): Write tests for the brand routes. Some ideas:
    //  - creating a valid brand returns 201, a Location header, and the brand with an id
    //  - a created brand can be fetched by id and matches what was sent
    //  - each validation rule returns 400 with a message in details
    //  - a body that isn't valid JSON returns 400
    //  - an unknown id returns 404
    //  - a non-numeric id returns whatever you decided in BrandRoutes.kt
    //  - prices keep their exact value, like "19.99" in and "19.99" out
    //
    //  Sending JSON looks like this:
    //    client.post("/brands") {
    //        contentType(ContentType.Application.Json)
    //        setBody(CreateBrandRequest(...))
    //    }
    //  For an invalid-JSON test, setBody("{ not json") sends the raw text instead.
}
