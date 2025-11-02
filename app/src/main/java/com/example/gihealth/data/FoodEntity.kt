package com.example.gihealth.data

import androidx.room.Entity
import androidx.room.PrimaryKey


//creating table for the food database
@Entity(tableName = "food_table")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) // id generate will increment
    //attributes for the table
    val id: Int = 0,
    val name: String
)