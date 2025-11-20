package com.example.gihealth.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_questions_table")
data class SecurityQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val question: String,
    val answer: String
)