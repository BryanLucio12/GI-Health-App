package com.example.gihealth.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy

@Dao
interface IngredientsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ingredient: IngredientsEntity)

    @Query("SELECT * FROM ingredients_table ORDER BY id ASC")
    suspend fun getAllIngredients(): List<IngredientsEntity>

    @Delete
    suspend fun delete(ingredient: IngredientsEntity)
}