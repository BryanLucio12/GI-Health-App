package com.example.gihealth.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SecurityQuestionEntity::class], version = 1)
abstract class SecurityQuestionDatabase : RoomDatabase() {

    abstract fun securityQuestionDao(): SecurityQuestionDao

    companion object {
        @Volatile
        private var INSTANCE: SecurityQuestionDatabase? = null

        fun getDatabase(context: Context): SecurityQuestionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SecurityQuestionDatabase::class.java,
                    "security_question_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}