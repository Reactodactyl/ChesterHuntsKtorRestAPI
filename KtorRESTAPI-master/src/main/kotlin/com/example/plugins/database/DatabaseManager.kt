package com.example.plugins.database

import com.example.plugins.entities.*
import com.example.plugins.utils.hashPass
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.support.mysql.insertOrUpdate
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class DatabaseManager {

    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    private val hostname = appConfig.property("db.hostname").getString()
    private val databaseName = appConfig.property("db.databaseName").getString()
    private val username = appConfig.property("db.username").getString()
    private val password = appConfig.property("db.password").getString()

    private val ktormDatabase: Database

    init {
        val jdbcUrl = "jdbc:mysql://$hostname:3306/$databaseName?user=$username&password=$password&useSSL=false"
        ktormDatabase = Database.connect(jdbcUrl)
    }

    //DBUSERTABLE FUCNTIONS
    fun getUserByNickName(nickname: String): DBUserEntity? {
        return ktormDatabase.sequenceOf(DBUserTable).firstOrNull {
            it.nickName eq nickname
        }
    }

    fun getUserByEmail(email: String): DBUserEntity? {
        return ktormDatabase.sequenceOf(DBUserTable).firstOrNull {
            it.emailAddress eq email
        }
    }

    fun getUserById(id: Int): DBUserEntity? {
        return ktormDatabase.sequenceOf(DBUserTable).firstOrNull {
            it.id eq id
        }
    }


    fun addUser(draft: UserDraft): DBUserEntity? {

        //id is generated automatically using below function
        val insertedId = ktormDatabase.insertAndGenerateKey(DBUserTable) {
            set(DBUserTable.nickName, draft.nickName)
            set(DBUserTable.password, draft.password.hashPass())
            set(DBUserTable.emailAddress, draft.email)
            set(DBUserTable.numofStamps, 0)
            set(DBUserTable.tokenverifier, BCrypt.hashpw(draft.nickName + draft.password, BCrypt.gensalt()))

        } as Int

        //return newly created user or null
        return getUserById(insertedId)
    }

    fun updateUser(id: Int, draft: UserDraft): Boolean {
        val updatedRows = ktormDatabase.update(DBUserTable) {
            set(DBUserTable.nickName, draft.nickName)
            set(DBUserTable.password, draft.password.hashPass())
            set(DBUserTable.emailAddress, draft.email)
            set(DBUserTable.tokenverifier, BCrypt.hashpw(draft.nickName + draft.password, BCrypt.gensalt()))
            where {
                it.id eq id
            }
        }
        return updatedRows > 0
    }

    fun removeUser(id: Int): Boolean {
        val deletedRows = ktormDatabase.delete(DBUserTable) {
            it.id eq id
        }
        return deletedRows > 0
    }

    //RESETCODEUSER FUCNTIONS

    //returns tuple with the latest reset code (no duplicates allowed)
    fun getResetUserById(resetuserid: Int): DBUserResetCodeEntity? {
        return ktormDatabase.sequenceOf(DBUserResetCodeTable).firstOrNull {
            it.resetUserid eq resetuserid
        }
    }


    //stores tuple with the latest reset code or updates existing tuple with latest reset code
    fun addResetUser(resetuser: ResetUser): Boolean {
        val updatedRows = ktormDatabase.insertOrUpdate(DBUserResetCodeTable) {
            set(DBUserResetCodeTable.resetUserid, resetuser.resetuserid)
            set(DBUserResetCodeTable.resetCode, resetuser.resetcode)
            set(DBUserResetCodeTable.dateCreated, resetuser.datecreated)
            onDuplicateKey {
                set(it.resetCode, resetuser.resetcode)// updates the resetCode
            }
        }
        return updatedRows > 0
    }

    // LEADERBOARD FUNCTIONS
    fun updateLeaderBoards(): Boolean {
        ktormDatabase.deleteAll(DBLeaderBoardsTable)
        var updatedRows = 0//check that leaderboard table was accessed

        //list of sorted users
        //please double check your workbench isn't already sorting :-)
        val leaders = ktormDatabase.sequenceOf(DBUserTable).sortedBy { it.numofStamps }.toList()

        //clear the leaderboard to avoid key conflicts
        
        for (i in leaders.indices.reversed()) {
            updatedRows = ktormDatabase.insert(DBLeaderBoardsTable) {
                set(DBLeaderBoardsTable.userId, leaders[i].id)
                set(DBLeaderBoardsTable.nickName, leaders[i].nickName)
                set(DBLeaderBoardsTable.numofStampsCollected, leaders[i].numofStamps)
            }
        }
        return updatedRows > 0
    }

    fun getLeaderBoards(): List<LeadersDTO> {
        //clear the leaderboard to avoid key conflicts
        ktormDatabase.deleteAll(DBLeaderBoardsTable)
        var updatedRows = 0//check that leaderboard table was accessed(implement a no response feature)

        //list of sorted users
        //please double check your workbench isn't already sorting :-)
        val leaders = ktormDatabase.sequenceOf(DBUserTable).sortedBy { it.numofStamps }.toList()

        //DTO conversion
        var retLeaders= mutableListOf<LeadersDTO>()


        for (i in leaders.indices.reversed()) {
            retLeaders.add(((leaders.size-1)-i) , LeadersDTO(leaders[i].id,leaders[i].nickName, leaders[i].numofStamps, ((leaders.size-1)-i)))
            updatedRows = ktormDatabase.insert(DBLeaderBoardsTable) {
                set(DBLeaderBoardsTable.userId, leaders[i].id)
                set(DBLeaderBoardsTable.nickName, leaders[i].nickName)
                set(DBLeaderBoardsTable.numofStampsCollected, leaders[i].numofStamps)

            }
        }

        //return new leaderboard to the user
        return retLeaders
    }

    //CURRENT EVENTS FUNCTIONS

    fun getAllCurrentEvents(): List<CurrentEventEntitySerializable>{

       val currentEventList= ktormDatabase.sequenceOf(DBCurrentEventTable).toList()

      var listCurrEventsEntities= mutableListOf<CurrentEventEntitySerializable>()

        //convert list to array list for serialization purposes
        for (i in currentEventList.indices) {
            listCurrEventsEntities.add(
                i,
                CurrentEventEntitySerializable(
                    currentEventList[i].idcurrentevent,
                   Date.from(currentEventList[i].startTime),
                    currentEventList[i].eventHost,
                    currentEventList[i].locationAddress,
                    currentEventList[i].Title,
                    currentEventList[i].subTitle,
                    currentEventList[i].details,
                    currentEventList[i].image1,
                    currentEventList[i].image2,
                    currentEventList[i].duration,
                    currentEventList[i].expandable,
                ))
        }
        return listCurrEventsEntities
    }

    fun getUserStamps(): List<Stamp> {

        val stampCollection = ktormDatabase.sequenceOf(DBStampTable).toList()

        val userStamps= mutableListOf<Stamp>()

        //convert list to array objection list for serialization purposes
        for (i in stampCollection.indices) {
            userStamps.add(
                i,
               Stamp(
                   stampCollection[i].idstamps,
                   stampCollection[i].photo,
                   stampCollection[i].title,
                   stampCollection[i].description,
                   stampCollection[i].hintText,
                   stampCollection[i].coordinateX,
                   stampCollection[i].coordinateY,
                   stampCollection[i].status,
                ))
        }
        return userStamps
    }

    fun getStampByCode(stampcode: String): DBStampTableEntity? {

        return ktormDatabase.sequenceOf(DBStampTable).firstOrNull {
            it.stampCode eq stampcode
        }
    }


}