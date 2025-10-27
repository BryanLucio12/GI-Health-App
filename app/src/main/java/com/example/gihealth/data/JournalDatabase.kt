package com.example.gihealth.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//Connecting Entity to Dao
@Database(entities = [JournalEntity::class], version = 1)
abstract class JournalDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao

    companion object {
        @Volatile
        private var INSTANCE: JournalDatabase? = null
        //ensure only one instance exists
        fun getDatabase(context: Context): JournalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JournalDatabase::class.java,
                    "journal_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}