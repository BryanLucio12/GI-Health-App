package com.example.gihealth.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//Connecting Entity to Dao
@Database(entities = [IngredientsEntity::class], version = 1)
abstract class IngredientsDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientsDao

    companion object {
        @Volatile
        private var INSTANCE: IngredientsDatabase? = null
        //ensure only one instance exists
        fun getDatabase(context: Context): IngredientsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IngredientsDatabase::class.java,
                    "ingredients_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}