package com.example.gihealth.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_catalog")
data class FoodCatalogEntity(
    @PrimaryKey val id: Long,      // fdcId
    val name: String,
    val ingredients: String
)
