package com.example.gihealth.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//connecting Entity to the Dao
@Database(
    entities = [
        SymptomEntity::class,
        CustomSymptomEntity::class
    ],
    version = 3
)
abstract class SymptomDatabase : RoomDatabase() {

    abstract fun symptomDao(): SymptomDao //give DAO
    abstract fun customSymptomDao(): CustomSymptomDao

    companion object {
        @Volatile
        private var INSTANCE: SymptomDatabase? = null
        //ensure that only one instance exists
        fun getDatabase(context: Context): SymptomDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SymptomDatabase::class.java,
                    "symptom_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
