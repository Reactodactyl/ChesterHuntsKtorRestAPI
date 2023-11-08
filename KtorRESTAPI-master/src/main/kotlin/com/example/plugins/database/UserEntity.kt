package com.example.plugins.database

import com.example.plugins.database.DBUserTable.bindTo
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object  DBUserTable: Table<DBUserEntity>("user"){
    val id = int("id").primaryKey().bindTo { it.id }
    val nickName = varchar("nickName").bindTo{it.nickName }
    val password = varchar("password").bindTo{it.password}
    val emailAddress = varchar("email").bindTo{it.email}
    val tokenverifier= varchar("tokenverifier").bindTo{it.tokenverifier}
    val numofStamps = int("numofStamps").bindTo{it.numofStamps}

}

interface DBUserEntity: Entity<DBUserEntity>{

    companion object: Entity.Factory<DBUserEntity>()

    val id: Int
    val nickName: String
    val password: String
    val email: String
    val tokenverifier: String
    val numofStamps:Int
}