package com.example.gihealth.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FoodCatalogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FoodCatalogEntity>)

    @Query("""
        SELECT * FROM food_catalog
        WHERE name LIKE '%' || :query || '%'
        ORDER BY name
        LIMIT 20
    """)
    suspend fun searchFoods(query: String): List<FoodCatalogEntity>
}