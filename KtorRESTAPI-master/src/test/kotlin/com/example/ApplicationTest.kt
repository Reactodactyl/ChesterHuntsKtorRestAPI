package com.example
import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.example.plugins.*


class ApplicationTest {
    @Test
    fun testRoot() {
        val token= "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJLdG9yQ2hlc3Rlckh1bnRzIiwiaXNzIjoiS3RvckNoZXN0ZXJIdW50cyIsImlkIjoxOSwidHlwZSI6InJlZnJlc2giLCJleHAiOjE2NDk5NjQ1NjEsImtleSI6IiQyYSQxMCRaNzhuNHB2TzZCTTRxdDBvY2Z3QVJPdi9VRWtCWGNwZFJYVE5IQ2pGN0JiRmpicldOdURNbSJ9.wGgFCLsqDLZwyToHCriOjYOEAEqLnhawDe4t4dGQEig"
        withTestApplication({ configureRouting() }) {
            handleRequest(HttpMethod.Get, "/refresh-token").apply {
                headersOf(HttpHeaders.ContentType, "Bearer $token")
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello World!", response.content)
            }
        }
    }
}