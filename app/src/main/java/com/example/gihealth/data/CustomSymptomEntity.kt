package com.example.gihealth.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_symptoms")
data class CustomSymptomEntity(
    @PrimaryKey
    val name: String
)