package com.example.gihealth.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomSymptomDao {

    @Query("SELECT * FROM custom_symptoms ORDER BY name ASC")
    fun getAllCustomSymptoms(): Flow<List<CustomSymptomEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(symptom: CustomSymptomEntity)
}