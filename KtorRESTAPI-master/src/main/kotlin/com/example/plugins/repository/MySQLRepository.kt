package com.example.plugins.repository


import com.example.plugins.database.DBCurrentEventEntity
import com.example.plugins.database.DBLeaderBoardsEntity
import com.example.plugins.database.DBStampTableEntity
import com.example.plugins.database.DatabaseManager
import com.example.plugins.entities.*
import com.example.plugins.utils.BaseResponse
import com.example.plugins.utils.JwtConfig
import com.example.plugins.utils.isValidCredentials
import com.example.plugins.utils.isValidLogInCredentials
import io.ktor.http.*
import org.mindrot.jbcrypt.BCrypt

class MySQLRepository : UserRepository {

    private val database = DatabaseManager()

//    override fun getAllUsers(): List<User> {
//       return database.getAllUsers().map{ User(it.id,it.firstName,it.nickName,it.email) }
//    }

    //return user if the object is not NULL
//    override fun getUserById(id: Int): User? {
//       return database.getUser( id )
//           ?.let{ User(it.id,it.firstName,it.nickName,it.email) }
//    }

    override fun getUserByNickName(nickName: String): User? {
        return database.getUserByNickName(nickName)
            ?.let { User(it.id, it.nickName, it.password, it.email, it.tokenverifier,it.numofStamps) }
    }

    override fun getUserByEmail(email: String): User? {
        return database.getUserByEmail(email)
            ?.let { User(it.id, it.nickName, it.password, it.email, it.tokenverifier,it.numofStamps) }
    }

    override  fun getUserById(id: Int): User? {
        return database.getUserById(id)
            ?.let { User(it.id, it.nickName, it.password, it.email, it.tokenverifier,it.numofStamps) }
        }


    override fun registerUser(draft: UserDraft): BaseResponse<Any> {

        if (!draft.isValidCredentials()) {
            return BaseResponse(data=HttpStatusCode.BadRequest, message = "Credentials entered are invalid")
        }

        if (database.getUserByEmail(draft.email) != null) {
            return BaseResponse(data=HttpStatusCode.Conflict,message = "Email already registered")
        }

        val nickname = draft.nickName.lowercase()

        if (database.getUserByNickName(nickname) != null) {
            return BaseResponse(data=HttpStatusCode.Conflict,message = "Nickname already taken")
        }

        val user = draft.copy(nickName = nickname)

        val userRegistered = database.addUser(user)

        return if (userRegistered != null) {
            val accessToken = JwtConfig.instance.createAccessToken(userRegistered.id,"access")
            val refreshToken = JwtConfig.instance.createRefreshToken(userRegistered.id,"refresh", userRegistered.tokenverifier)

            val retUser = UserResponse(
                userRegistered.id,
                userRegistered.nickName,
                userRegistered.email,
                accessToken,
                refreshToken,
            0
            )

            BaseResponse(data = retUser)
        } else {
            BaseResponse(data= HttpStatusCode.InternalServerError,message = "Unable to register user")
        }

    }

    override fun loginUser(loginRequest: LoginRequest): BaseResponse<Any> {
        if (!loginRequest.isValidLogInCredentials()) {
            return BaseResponse(HttpStatusCode.BadRequest, message = "Credentials entered are invalid")
        }

        val email = loginRequest.email
        val password = loginRequest.password
        val refresktk =loginRequest.refreshTkCheck

        val user = database.getUserByEmail(email)
            ?: return BaseResponse(HttpStatusCode.NotFound, message = "Incorrect email or password")

        val passwrdMatch = BCrypt.checkpw(password, user.password)

        if (!passwrdMatch) {
            return BaseResponse(HttpStatusCode.NotFound, message = "Incorrect email or password")
        }

        // generates new access token to be access protected path
        val userResponse: UserResponse = if (refresktk) {
            val token = JwtConfig.instance.createAccessToken(user.id, "access")
            UserResponse(user.id, user.nickName, user.email, token, "", user.numofStamps)
        } else {
            val accessToken = JwtConfig.instance.createAccessToken(user.id, "access")
            val refreshToken = JwtConfig.instance.createRefreshToken(user.id, "refresh", user.tokenverifier)

            UserResponse(user.id, user.nickName, user.email, accessToken, refreshToken, user.numofStamps)
        }

        return BaseResponse(data = userResponse)
    }

    override fun updateUser(id: Int, draft: UserDraft): Boolean {
        return database.updateUser(id, draft)
    }

    override fun removeUser(id: Int): Boolean {
        return database.removeUser(id)
    }

    override fun addResetUser(resetuser: ResetUser): Boolean{
        return database.addResetUser(resetuser)
    }

    override fun getResetUserById(resetuserid: Int): ResetUser?{
       val user = database.getResetUserById(resetuserid)

        if (user != null) {
            return ResetUser(resetuserid=user.resetUserid, resetcode = user.resetCode, datecreated = user.datecreated, )
        }
        return null

    }

    override fun updateLeaderBoards(): Boolean{
        return database.updateLeaderBoards()

    }

    override fun getAllCurrentEvents(): List<CurrentEventEntitySerializable> {
       return database.getAllCurrentEvents()
    }

    override fun getUserStamps(): List<Stamp>{
        return database.getUserStamps()
    }

    override fun getStampByCode(stampCode: String): Stamp?{
        val foundStamp = database.getStampByCode(stampCode)

        if(foundStamp!= null){
            return Stamp(foundStamp.idstamps,
                foundStamp.photo,
                foundStamp.title,
                foundStamp.description,
                foundStamp.hintText,
                foundStamp.coordinateX,
                foundStamp.coordinateY,
                foundStamp.status,
                )
        }
        return null
    }

    override fun getLeaderBoards(): List<LeadersDTO>{
        return database.getLeaderBoards()
    }


    //override fun getPlaces(): List<Stamp>

}