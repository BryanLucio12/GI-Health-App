package com.example.gihealth.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//connecting Entity to the Dao

@Database(entities = [UserInfoEntity::class], version = 3)
abstract class UserInfoDatabase : RoomDatabase() {

    abstract fun UserInfoDao(): UserInfoDao
    companion object {
        @Volatile
        private var INSTANCE: UserInfoDatabase? = null

        //ensure that only one instance exists
        fun getDatabase(context: Context): UserInfoDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserInfoDatabase::class.java,
                    "user_info_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
