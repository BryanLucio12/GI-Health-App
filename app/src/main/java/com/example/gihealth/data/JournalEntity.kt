package com.example.gihealth.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//creating table for the journal database
@Entity(tableName = "journal_table")
data class JournalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val entry: String,
    val timestamp: Long = System.currentTimeMillis()
)
