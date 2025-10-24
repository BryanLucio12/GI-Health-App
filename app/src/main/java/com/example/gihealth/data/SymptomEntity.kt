package com.example.gihealth.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp


//creating table for the  symptom database
@Entity(tableName = "symptom_table")
data class SymptomEntity(
    @PrimaryKey(autoGenerate = true) //id generate will increment
    //attributes for the table
    val id: Int = 0,
    val name: String,
    val severity: Int,        //1-5
    val timestamp: Long,      //datetime when symptom logged
    val timeLength: Int       //duration in hours
)