package com.example.gihealth.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//creating table for the ingredients database
@Entity(tableName = "ingredients_table")
data class IngredientsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: String
)
