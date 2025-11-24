package com.example.gihealth.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow



data class TopSymptomResults(
    val name: String,
    val count: Int,
    val avgSeverity: Double
)


data class DailyStatus(
    val day: String,
    val totalSymptoms: Int,
    val maxSeverity: Int,
    val avgSeverity: Double,
    val totalDuration: Int,
    val dayStatus: String       //good or bad
)

//giving the methods the database has access to insert, newest symptom, delete symptom
@Dao
interface SymptomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(symptom: SymptomEntity)

    @Query("SELECT * FROM symptom_table ORDER BY timestamp DESC")
    fun getAllSymptoms(): Flow<List<SymptomEntity>>


    @Delete
    suspend fun delete(symptom: SymptomEntity)


    //Analytic queries
    //top 3 symtpoms and avg severity level
    @Query("""
        SELECT name, COUNT(name) AS count, AVG(severity) as avgSeverity
        FROM symptom_table
        WHERE severity>0
        GROUP BY name
        ORDER BY count DESC
        LIMIT 3
        """)
    fun top3SymptomsWithSeverity(): Flow<List<TopSymptomResults>>


    //show weekly serverity list last 7 days for pdf symptoms report
    @Query("""
        SELECT *
        FROM symptom_table
        WHERE timestamp>= strftime("%s", 'now','-7 days') * 1000
        ORDER BY timestamp DESC
    """)
    fun weeklySeverities(): Flow<List<SymptomEntity>>



    //good and bad day with edge cases
    @Query("""
    SELECT 
        DATE(timestamp / 1000, 'unixepoch') AS day,
        COUNT(*) AS totalSymptoms,
        MAX(severity) AS maxSeverity,
        AVG(severity) AS avgSeverity,
        SUM(timeLength) AS totalDuration,
        
        CASE
            WHEN MAX(severity) >= 8 THEN 'BAD'
            WHEN COUNT(*) > 3 THEN 'BAD'
            WHEN AVG(severity) >= 6 THEN 'BAD'
            WHEN SUM(timeLength) >= 5 THEN 'BAD'
            WHEN MAX(timeLength) >= 3 THEN 'BAD'
            ELSE 'GOOD'
        END AS dayStatus

    FROM symptom_table
    GROUP BY day
    ORDER BY day DESC
""")
    fun getDailyStatuses(): Flow<List<DailyStatus>>



}




