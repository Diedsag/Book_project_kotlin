package com.example.mainprojectkt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mainprojectkt.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE) suspend fun addUser(user: UserEntity): Long
    @Update suspend fun updateUser(user: UserEntity)
    @Delete suspend fun deleteUser(user: UserEntity)
    @Query ("select * from Users") fun getUsers(): Flow<List<UserEntity>>
}