package com.example.plugins


import com.example.plugins.utils.JwtConfig
import com.typesafe.config.ConfigFactory
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.application.*
import io.ktor.server.config.*


fun Application.configureSecurity() {

    val appConfig = HoconApplicationConfig( ConfigFactory.load() )
    val secret = appConfig.property("jwt.secret").getString()

    JwtConfig.initialize(secret)
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(JwtConfig.instance.verifier)

            validate { credential ->

                val claimType = credential.payload.getClaim(JwtConfig.TYPE).asString()
                val claimId = credential.payload.getClaim(JwtConfig.ID).asInt()

                if (claimType != null && claimId != null) {

                    //print("valid claim\n")

                    JWTPrincipal(credential.payload)

                } else {
                    null
                }

            }
        }
    }
//
//        oauth("auth-oauth-google") {
//            urlProvider = { "http://localhost:8080/callback" }
//            providerLookup = {
//                OAuthServerSettings.OAuth2ServerSettings(
//                    name = "google",
//                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
//                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
//                    requestMethod = HttpMethod.Post,
//                    clientId = System.getenv("GOOGLE_CLIENT_ID"),
//                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
//                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile")
//                )
//            }
//            client = httpClient
//        }
//    }
}
