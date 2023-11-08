package com.example.plugins

import com.example.Constants.ONE_WEEK_MILLI
import com.example.Constants.RESET_CODE_VALID_TIME
import com.example.plugins.entities.*
import com.example.plugins.repository.MySQLRepository
import com.example.plugins.repository.UserRepository
import com.example.plugins.utils.JwtConfig
import com.example.plugins.utils.getResetEmailTemplate
import com.example.plugins.utils.isValidEmail
import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.axay.simplekotlinmail.delivery.mailerBuilder
import net.axay.simplekotlinmail.delivery.send
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant
import java.util.*

fun Application.configureRouting() {


    routing {

        trace { application.log.trace(it.buildText()) }

        val repository: UserRepository = MySQLRepository()
        val appConfig = HoconApplicationConfig(ConfigFactory.load())

        val mailer = mailerBuilder(
            username = appConfig.property("emailServer.authEmail").getString(),
            password = appConfig.property("emailServer.authEmailPassword").getString(),
            host = "smtp.gmail.com",
            port = 587
        )

        authenticate("auth-jwt") {
            get("/me") {

                val params = call.principal<JWTPrincipal>()

                val userId = params?.payload?.getClaim("id")?.asInt()
                val claimType = params?.payload?.getClaim("type")?.asString()

                if (userId == null || claimType != "access") {
                    call.respond(HttpStatusCode.BadRequest, ServerResponse(message = "invalid token"))
                } else {
                    val user: User? = repository.getUserById(userId)

                    if (user != null) {
                        val retUser = UserResponse(
                            user.id,
                            user.nickName,
                            user.email,
                            "",
                            "",
                            user.numofStamps
                        )
                        call.respond(HttpStatusCode.OK, retUser)
                    }
                    call.respond(HttpStatusCode.BadRequest, ServerResponse(message = " Cannot find User"))
                }
            }


            get("/refresh-token") {
                val params = call.principal<JWTPrincipal>()

                val userId = params?.payload?.getClaim("id")?.asInt()
                val claimType = params?.payload?.getClaim("type")?.asString()
                val tokenKey = params?.payload?.getClaim("key")?.asString()

                if (userId == null || claimType != "refresh") {
                    call.respond(HttpStatusCode.BadRequest, ServerResponse("Error could not issue new token"))
                } else {
                    val user: User? = repository.getUserById(userId)

                    if (user == null) {
                        call.respond(HttpStatusCode.BadRequest, ServerResponse("User not found "))
                    } else if (user.tokenverifier == tokenKey) {
                        print("key match\n")

                        // checks if the key will expire within two weeks and issues a new one
                        if (params.payload.expiresAt.before(Date(System.currentTimeMillis() + ONE_WEEK_MILLI))) {
                            val newAccessToken = JwtConfig.instance.createAccessToken(user.id, "access")
                            val newRefreshToken =
                                JwtConfig.instance.createRefreshToken(user.id, "refresh", user.tokenverifier)

                            val tokenResponse = TokenResponse(newAccessToken, newRefreshToken)
                            call.respond(HttpStatusCode.OK, tokenResponse)
                        } else {
                            val newAccessToken = JwtConfig.instance.createAccessToken(user.id, "access")
                            val tokenResponse = TokenResponse(newAccessToken, "")//no new refresh token

                            call.respond(HttpStatusCode.OK, tokenResponse)
                        }
                    }
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            post("/update-pw"){
                val params = call.principal<JWTPrincipal>()

                val userUpdatedPass= call.receive<UpdatedPassword>()

                //check that the token is the correct type
                val userId = params?.payload?.getClaim("id")?.asInt()
                val claimType = params?.payload?.getClaim("type")?.asString()

                if (userId == null || claimType != "access") {
                    call.respond(HttpStatusCode.NotFound )
                }
                else{
                    val user = repository.getUserById(userId)

                    if(user==null){
                        call.respond(HttpStatusCode.NotFound,ServerResponse("user not found" ))
                    }
                    else{
                        val passwrdMatch = BCrypt.checkpw(userUpdatedPass.newPassword, user.password)

                        if(passwrdMatch){
                            call.respond(HttpStatusCode.Conflict,message="new password and old password are the same" )
                        }else{
                            val updateUser=UserDraft(user.nickName,userUpdatedPass.newPassword,user.email)
                                repository.updateUser(user.id, updateUser)

                                call.respond(HttpStatusCode.OK,ServerResponse("password update successful"))
                            }
                        }
                    }
                }

            }//END of AUTH BLOCK

        route("/resources"){

            get("/current-events")
            {
                val currentEvents = repository.getAllCurrentEvents()

                call.respond(HttpStatusCode.OK,currentEvents)
            }

            get("/places/{query_event_id}")
            {
                val eventId = call.parameters["query_event_id"]?.toInt()

                if ((eventId == null) || (eventId < 0) || (eventId > 9)) {
                    call.respond(HttpStatusCode.BadRequest,ServerResponse("event does not exists"))
                }
                else{
                        //val eventCoordinates= repository.getPlaces(eventId)
                }


            }


            get("/my-stamps"){

                val stamps = repository.getUserStamps()

                call.respond(HttpStatusCode.OK,stamps)
            }

            post("/stamp-find"){
                val stampCode = call.receive<StampCode>()

                val usrstampCode = stampCode.stampcode

                val stamp = repository.getStampByCode(usrstampCode)

                if (stamp != null)
                    call.respond(HttpStatusCode.OK, stamp)
                else
                    call.respond(HttpStatusCode.BadRequest, ServerResponse("stamp not found"))

            }

            get("/leaderboards")
            {
                val leaders = repository.getLeaderBoards()

                call.respond(HttpStatusCode.OK,leaders)
            }
        }

        route("/auth") {

            get("/") {
                call.respondText("Hello User!")
            }

            post("/register") {
                val params = call.receive<UserDraft>()
                val result = repository.registerUser(params)

                if (result.message.isNullOrEmpty()) {
                    val userResponse: UserResponse = result.data as UserResponse
                    call.respond(HttpStatusCode.OK, userResponse)
                } else {
                    val errorCode: HttpStatusCode = result.data as HttpStatusCode
                    call.respond(errorCode, ServerResponse(result.message))
                }
            }

            post("/login") {
                val params = call.receive<LoginRequest>()
                val result = repository.loginUser(params)

               val ex =repository.updateLeaderBoards()

                if(ex)
                    print("\n\n\nleaderboards have been updated")
                if (result.message.isNullOrEmpty()) {
                    val userResponse: UserResponse = result.data as UserResponse
                    call.respond(HttpStatusCode.OK, userResponse)
                } else {
                    val errorCode: HttpStatusCode = result.data as HttpStatusCode
                    call.respond(errorCode, ServerResponse(result.message))
                }

            }

            post("/reset-pw") {
                val resetrequest = call.receive<ResetRequest>()
                val resetEmail = resetrequest.email

                if (!resetEmail.isValidEmail()) {
                    call.respond(HttpStatusCode.BadRequest, ServerResponse("email invalid"))
                } else {
                    val user: User? = repository.getUserByEmail(resetEmail)

                    if (user == null /*&& TODO the user is verified*/) {
                        call.respond(HttpStatusCode.BadRequest, ServerResponse("email not registered/verified"))

                    } else {

                        val resetUser = repository.getResetUserById(user.id)

                        //does not issue a reset code if existing one is valid

                        if (resetUser != null && (System.currentTimeMillis() - resetUser.datecreated.toEpochMilli() < RESET_CODE_VALID_TIME)) {
                            call.respond(HttpStatusCode.BadRequest, ServerResponse("New reset code cannot be issued"))
                        } else {
                            val resetCode = (1000..9999).random()

                            val resetMessage = getResetEmailTemplate(resetEmail, user.nickName, resetCode)

                            kotlin.runCatching { resetMessage.send(mailer).join() }
                                .onFailure {
                                    it.printStackTrace()
                                    call.respond(HttpStatusCode.InternalServerError, ServerResponse("email not sent"))
                                }
                                .onSuccess {

                                    // new record only after the email has been delivered
                                    val resetUsr = ResetUser(user.id, resetCode, Instant.now())
                                    repository.addResetUser(resetUsr)

                                   call.respond(HttpStatusCode.OK, ServerResponse("reset code has been sent"))
                                }
                             }
                        }
                    }
                }

            post("reset-code") {
                val userResetDto = call.receive<ResetPwDTO>()

                //validate the data within the DTO
                if (!userResetDto.email.isValidEmail() && (userResetDto.resetCode in 1000..9999)) {
                    call.respond(HttpStatusCode.BadRequest, ServerResponse(" reset error occurred"))
                } else {
                    val registeredUser = repository.getUserByEmail(userResetDto.email)

                    if (registeredUser == null) {
                        call.respond(HttpStatusCode.BadRequest, ServerResponse(" reset error occurred"))
                    } else {
                        val resetUser = repository.getResetUserById(registeredUser.id)

                        if (resetUser == null) {
                            call.respond(HttpStatusCode.BadRequest, ServerResponse("reset code not found"))
                        } else {
                            if (System.currentTimeMillis() - resetUser.datecreated.toEpochMilli() > RESET_CODE_VALID_TIME) {
                                call.respond(
                                    HttpStatusCode.BadRequest,
                                    ServerResponse("reset code has expired. Please request new reset code"))
                            } else if (System.currentTimeMillis() - resetUser.datecreated.toEpochMilli() < RESET_CODE_VALID_TIME) {
                                if (userResetDto.resetCode == resetUser.resetcode) {
                                    val accessToken = JwtConfig.instance.createAccessToken(resetUser.resetuserid,"access")
                                    call.respond(HttpStatusCode.OK,message=TokenResponse(accessToken,""))
                                }
                            }
                        }
                    }
                    call.respond(HttpStatusCode.BadRequest, ServerResponse("reset code is incorrect/invalid"))
                }
            }
        }
    }
}



//        authenticate("auth-oauth-google") {
//            get("/login") {
//                // Redirects to 'authorizeUrl' automatically
//            }
//
//            get("/callback") {
//                val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
//                call.sessions.set(UserSession(principal?.accessToken.toString()))
//                call.respondRedirect("/hello")
//            }
//        }

//        get("/users"){
//            call.respond(repository.getAllUsers())
//        }
//
//        get("/users/{id}"){
//            val id =call.parameters["id"]?.toIntOrNull()
//
//            if(id == null){
//                call.respond(HttpStatusCode.BadRequest,"id parameter has to be number")
//                return@get
//            }
//
//            val user =repository.getUserById(id)
//
//            if( user == null ){
//                call.respond(HttpStatusCode.NotFound,"found no user with given id $id")
//            }
//            else{
//                call.respond(user)
//            }
//        }
//
//        post("/users"){
//            val userDraft = call.receive<UserDraft>()
//            val user = repository.addUser(userDraft)
//            call.respond(user)
//        }
//
//        put("users/{id}"){
//            val userDraft=call.receive<UserDraft>()
//            val userId = call.parameters["id"]?.toIntOrNull()
//
//            if(userId==null){
//                call.respond(HttpStatusCode.BadRequest,"id parameter has to be a number")
//                return@put
//            }
//
//            val updated = repository.updateUser(userId,userDraft)
//
//            if(updated){
//                call.respond(HttpStatusCode.OK)
//            }
//            else{
//                call.respond(HttpStatusCode.NotFound,"User with id $userId not found")
//            }
//        }
//
//        delete("/users/{id}"){
//            val userId = call.parameters["id"]?.toIntOrNull()
//
//            if(userId == null){
//                call.respond(HttpStatusCode.BadRequest,"id parameter has to be a number")
//                return@delete
//            }
//
//            val removed = repository.removeUser(userId)
//
//            if(removed){
//                call.respond(HttpStatusCode.OK)
//            }
//            else{
//                call.respond(HttpStatusCode.NotFound,"User with id:$userId not found")
//            }
//        }

