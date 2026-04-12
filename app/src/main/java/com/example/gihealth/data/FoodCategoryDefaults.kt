package com.example.gihealth.data

object FoodCategoryDefaults {

    val categoryOptions = listOf(
        "Dairy",
        "Gluten",
        "Meat",
        "Spicy",
        "Fried",
        "Caffeine",
        "High Sugar",
        "High Fiber"
    )

    private val defaultFoodCategoryMap = mapOf(
        "pizza" to listOf("Dairy", "Gluten", "Meat"),
        "cheese pizza" to listOf("Dairy", "Gluten"),
        "pepperoni pizza" to listOf("Dairy", "Gluten", "Meat"),
        "burger" to listOf("Gluten", "Meat"),
        "cheeseburger" to listOf("Dairy", "Gluten", "Meat"),
        "milk" to listOf("Dairy"),
        "ice cream" to listOf("Dairy", "High Sugar"),
        "coffee" to listOf("Caffeine"),
        "fried chicken" to listOf("Meat", "Fried"),
        "chicken sandwich" to listOf("Gluten", "Meat"),
        "pasta" to listOf("Gluten"),
        "mac and cheese" to listOf("Dairy", "Gluten")
    )

    fun getSuggestedCategories(foodName: String): List<String> {
        return defaultFoodCategoryMap[foodName.trim().lowercase()] ?: emptyList()
    }
}