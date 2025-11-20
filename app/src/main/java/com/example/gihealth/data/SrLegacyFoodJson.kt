// SrLegacyFoodJson.kt
package com.example.gihealth.data

data class SrLegacyFoodJson(
    val fdcId: Long,
    val description: String,
    val foodCategory: FoodCategoryJson?
)

data class FoodCategoryJson(
    val description: String?
)