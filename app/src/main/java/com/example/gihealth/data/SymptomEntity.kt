package com.example.gihealth.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "symptom_table")
data class SymptomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val severity: Int,
    val timestamp: Long,
    val timeLength: Int
)