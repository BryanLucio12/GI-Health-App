package com.example.gihealth.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wellbeing_table")
data class WellBeingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long,
    val weight: Float,
    val unit: String,
    val sleepRating: Int,
    val sleepNote: String,
    val stressRating: Int,
    val stressNote: String,
    val date: String,
    val looseStoolsCount: Int = 0
)