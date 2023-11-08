package com.example.plugins.database



import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.time.Instant

object DBUserResetCodeTable : Table<DBUserResetCodeEntity>("userresetcode") {
    val resetUserid = int("idresetuser").primaryKey().bindTo { it.resetUserid }
    val resetCode = int("resetcode").bindTo { it.resetCode }
    val dateCreated = timestamp("datecreated").bindTo { it.datecreated }

}

interface DBUserResetCodeEntity : Entity<DBUserResetCodeEntity> {

    companion object : Entity.Factory<DBUserResetCodeEntity>()

    val resetUserid: Int
    val resetCode: Int
    val datecreated: Instant
}