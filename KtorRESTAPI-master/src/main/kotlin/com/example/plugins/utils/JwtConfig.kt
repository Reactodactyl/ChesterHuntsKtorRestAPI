package com.example.plugins.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.Constants.ONE_MONTH
import com.example.Constants.ONE_WEEK_MILLI
import com.example.Constants.RESET_CODE_VALID_TIME
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import java.util.*

class JwtConfig private constructor(secretString: String) {

    private val algorithm = Algorithm.HMAC256(secretString)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .build()

    // generate access token for the user
    fun createAccessToken(id: Int, type: String): String = JWT
        .create()
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .withClaim(ID, id)
        .withClaim(TYPE, type)
        .withExpiresAt(Date(System.currentTimeMillis() + RESET_CODE_VALID_TIME))//valid for 5 mins
        .sign(algorithm)

    // generate refresh token for the user
    fun createRefreshToken(id: Int, type: String, key: String): String = JWT
        .create()
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .withClaim(ID, id)
        .withClaim(TYPE, type)
        .withClaim(KEY, key)
        .withExpiresAt(Date(System.currentTimeMillis() + ONE_WEEK_MILLI))//valid for 1 month
        .sign(algorithm)


    companion object {
        private val appConfig = HoconApplicationConfig( ConfigFactory.load() )
        private  val ISSUER = appConfig.property("jwt.issuer").getString()
        private  val AUDIENCE = appConfig.property("jwt.audience").getString()
         val ID = appConfig.property("jwt.id").getString()//user id
         val KEY = appConfig.property("jwt.key").getString()// only for refresh token(for validation check)
         val TYPE = appConfig.property("jwt.type").getString()//token type

        //only want to access instance with fun init
        lateinit var instance: JwtConfig
            private set

        fun initialize(secretString: String) {
            synchronized(!this::instance.isInitialized) {
                instance = JwtConfig(secretString)

            }
        }
    }
}
