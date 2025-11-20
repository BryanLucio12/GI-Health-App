package com.example.gihealth.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy

@Dao
interface SecurityQuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: SecurityQuestionEntity)

    @Query("SELECT * FROM security_questions_table ORDER BY id ASC")
    suspend fun getAllQuestions(): List<SecurityQuestionEntity>

    @Query("DELETE FROM security_questions_table")
    suspend fun deleteAll()
}