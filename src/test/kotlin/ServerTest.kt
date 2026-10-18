package io.github.tdavi156.listingwatch

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.*

class ServerTest {

    @Test
    fun `health returns status`() = testApplication {
        configure()
        assertEquals(HttpStatusCode.OK, client.get("/health").status)
    }

    @Test
    fun `health returns text`() = testApplication {
        configure()
        assertEquals("OK", client.get("/health").bodyAsText())
    }

    @Test
    fun `unknown path returns 404`() = testApplication {
        configure()
        assertEquals(HttpStatusCode.NotFound, client.get("/does-not-exist").status)
    }

    @Test
    fun `root path is gone`() = testApplication {
        configure()
        assertEquals(HttpStatusCode.NotFound, client.get("/").status)
    }

    @Test
    fun `wrong method on health is rejected`() = testApplication {
        configure()
        assertEquals(HttpStatusCode.MethodNotAllowed, client.post("/health").status)
    }

}
