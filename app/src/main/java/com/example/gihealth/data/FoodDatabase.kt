package com.example.gihealth.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FoodEntity::class, FoodCatalogEntity::class],
    version = 7
)
abstract class FoodDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao
    abstract fun foodCatalogDao(): FoodCatalogDao

    companion object {
        @Volatile
        internal var INSTANCE: FoodDatabase? = null   // <- made internal so callback can see it

        fun getDatabase(context: Context): FoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodDatabase::class.java,
                    "food_database"
                )
                    .fallbackToDestructiveMigration() // okay while still developing
                    .addCallback(SeedCatalogCallback(context.applicationContext))
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
