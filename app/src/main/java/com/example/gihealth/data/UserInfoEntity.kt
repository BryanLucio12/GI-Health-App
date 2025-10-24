package com.example.gihealth.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

//creating table for the  Userinfo database
@Entity(tableName = "user_info_table")
data class UserInfoEntity(
    @PrimaryKey(autoGenerate = true)     ////id generate will increment
    val id: Int = 0,
    val name: String,
    val age: Int,
    val bloodType: String,
    val weight: Float,
    val disease: String
)