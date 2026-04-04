package com.example.gihealth.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BloodworkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bloodwork: BloodworkEntity)

    @Query("SELECT * FROM bloodwork_table ORDER BY id ASC")
    suspend fun getAllBloodwork(): List<BloodworkEntity>

    @Query("SELECT * FROM bloodwork_table WHERE date = :date ORDER BY id ASC")
    suspend fun getBloodworkForDate(date: String): List<BloodworkEntity>

    @Delete
    suspend fun delete(bloodwork: BloodworkEntity)
}