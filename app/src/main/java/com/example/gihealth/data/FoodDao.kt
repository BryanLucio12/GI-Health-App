package com.example.gihealth.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: FoodEntity)

    @Query("SELECT * FROM food_table ORDER BY id ASC")
    suspend fun getAllFoods(): List<FoodEntity>

    @Query("SELECT * FROM food_table WHERE date = :date ORDER BY id ASC")
    suspend fun getFoodsForDate(date: String): List<FoodEntity>

    @Query("SELECT date FROM food_table WHERE name = :foodName")
    suspend fun getDatesForFood(foodName: String): List<String>

    @Query("SELECT DISTINCT name FROM food_table ORDER BY name ASC")
    suspend fun getDistinctFoodNames(): List<String>

    @Query("""
        SELECT name
        FROM food_table
        WHERE name LIKE '%' || :query || '%' COLLATE NOCASE
        GROUP BY name
        ORDER BY MAX(id) DESC
        LIMIT 8
    """)
    suspend fun searchLoggedFoodNames(query: String): List<String>

    @Delete
    suspend fun delete(food: FoodEntity)
}