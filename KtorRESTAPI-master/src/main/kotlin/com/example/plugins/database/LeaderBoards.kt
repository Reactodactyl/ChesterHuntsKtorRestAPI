package com.example.plugins.database


import com.example.plugins.database.DBUserResetCodeTable.bindTo
import com.example.plugins.database.DBUserResetCodeTable.primaryKey
import com.example.plugins.database.DBUserTable.bindTo
import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.sql.Timestamp
import java.time.Instant

object DBLeaderBoardsTable : Table<DBLeaderBoardsEntity>("leaderboards") {
    val userId = int("userid").primaryKey().bindTo { it. userId }
    val nickName = varchar("nickname").bindTo { it.nickName }
    val numofStampsCollected = int("numofstamps").bindTo { it.numofStamps }

}

interface DBLeaderBoardsEntity : Entity<DBLeaderBoardsEntity> {

    companion object : Entity.Factory<DBLeaderBoardsEntity>()

    val userId: Int
    val nickName: String
    val numofStamps: Int
}