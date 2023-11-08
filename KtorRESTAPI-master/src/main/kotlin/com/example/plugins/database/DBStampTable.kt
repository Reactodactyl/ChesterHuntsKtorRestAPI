package com.example.plugins.database

import org.ktorm.entity.Entity
import org.ktorm.schema.*

object DBStampTable : Table<DBStampTableEntity>("stamps") {
    val idstamps=int("idstamps").primaryKey().bindTo { it.idstamps }
    val name=varchar("name").bindTo { it.name }
    val photo = varchar("photo").bindTo { it.photo }
    val title= varchar("title").bindTo { it.title }
    val description = varchar("description").bindTo { it.description }
    val hintText = varchar("hintText").bindTo { it.hintText }
    val coordinateX = int("coordinateX").bindTo { it.coordinateX }
    val coordinateY = int("coordinateY").bindTo { it.coordinateY }
    val status = boolean("status").bindTo { it.status }
    val stampCode= varchar("stampsCode").bindTo { it.stampsCode }

}

interface DBStampTableEntity : Entity<DBStampTableEntity> {

    companion object : Entity.Factory<DBStampTableEntity>()

    val idstamps: Int
    val photo: String
    val title: String
    val name: String
    val description: String
    val hintText: String
    val coordinateX: Int
    val coordinateY: Int
    val status:Boolean
    val stampsCode: String
}