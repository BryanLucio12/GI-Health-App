package com.example.gihealth.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy

@Dao
interface JournalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(journal: JournalEntity)

    @Query("SELECT * FROM journal_table ORDER BY timestamp DESC")
    suspend fun getAllJournals(): List<JournalEntity>

    @Delete
    suspend fun delete(journal: JournalEntity)
}