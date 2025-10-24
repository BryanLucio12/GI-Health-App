package com.example.gihealth.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy

//methods the database has access to can Insert, query info, and delete
@Dao
interface UserInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userInfoD: UserInfoEntity)

    @Query("SELECT * FROM user_info_table Limit 1")
    suspend fun getUserInfo(): UserInfoEntity?

    @Delete
    suspend fun delete(userInfo: UserInfoEntity)
}

