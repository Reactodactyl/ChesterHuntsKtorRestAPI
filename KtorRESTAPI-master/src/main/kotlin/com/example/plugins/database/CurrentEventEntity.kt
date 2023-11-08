package com.example.plugins.database


import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.sql.Timestamp
import java.time.Instant

object DBCurrentEventTable : Table<DBCurrentEventEntity>("currentevent") {
    val idcurrentevent = int("idcurrentevent").primaryKey().bindTo { it.idcurrentevent }
    val startTime = timestamp("StartTime").bindTo { it.startTime }
    val eventHost = varchar("EventHost").bindTo { it.eventHost }
    val locationAddress = varchar("LocationAddress").bindTo { it.locationAddress }
    val Title =varchar("Title").bindTo{it.Title}
    val SubTitle =varchar("SubTitle").bindTo{it.subTitle}
    val Details =varchar("Details").bindTo{it.details}
    val Image1 =varchar("Image1").bindTo{it.image1}
    val Image2 =varchar("Image2").bindTo{it.image2}
    val Duration =int("Duration").bindTo{it.duration}
    val Expandable =boolean("Expandable").bindTo{it.expandable}


}

interface DBCurrentEventEntity : Entity<DBCurrentEventEntity> {

    companion object : Entity.Factory<DBCurrentEventEntity>()

    val idcurrentevent: Int
    val startTime: Instant
    val eventHost: String
    val locationAddress: String
    val Title: String
    val subTitle: String
    val details: String
    val image1:String
    val image2:String
    val duration:Int
    val expandable: Boolean
}