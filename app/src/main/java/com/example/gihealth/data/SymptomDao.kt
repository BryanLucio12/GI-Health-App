package com.example.gihealth.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow



//giving the methods the database has access to insert, newest symptom, delete symptom
@Dao
interface SymptomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(symptom: SymptomEntity)

    @Query("SELECT * FROM symptom_table ORDER BY timestamp DESC")
    fun getAllSymptoms(): Flow<List<SymptomEntity>>

    @Delete
    suspend fun delete(symptom: SymptomEntity)
}



