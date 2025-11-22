package com.example.gihealth.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy

@Dao
interface WellBeingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WellBeingEntity)

    @Query("SELECT * FROM wellbeing_table ORDER BY timestamp DESC")
    suspend fun getAllEntries(): List<WellBeingEntity>

    @Delete
    suspend fun delete(entry: WellBeingEntity)
}