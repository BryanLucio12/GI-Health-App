package com.example.gihealth.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bloodwork_table")
data class BloodworkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val b12Level: Double? = null,
    val crpLevel: Double? = null
)