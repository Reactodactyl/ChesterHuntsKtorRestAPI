package com.example.plugins.repository

import com.example.plugins.database.*
import com.example.plugins.entities.*
import com.example.plugins.utils.BaseResponse

interface UserRepository {

    //fun getAllUsers(): List<User>

    //fun getUserById(id: Int): User?

    fun getUserByNickName(nickName: String): User?

    fun getUserByEmail(email: String): User?

    fun getUserById(id:Int): User?


    fun registerUser(draft: UserDraft): BaseResponse<Any>

    fun loginUser(loginRequest: LoginRequest): BaseResponse<Any>

    fun updateUser(id: Int, draft: UserDraft): Boolean

    fun removeUser(id: Int): Boolean


    fun addResetUser(resetuser: ResetUser): Boolean

    fun getResetUserById(resetuserid: Int): ResetUser?


    fun updateLeaderBoards(): Boolean

    fun getLeaderBoards(): List<LeadersDTO>




    fun getAllCurrentEvents(): List<CurrentEventEntitySerializable>

    fun getUserStamps(): List<Stamp>

    fun getStampByCode(stampCode : String): Stamp?


}