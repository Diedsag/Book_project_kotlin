package com.example.mainprojectkt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mainprojectkt.data.local.entity.UserBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserBookDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE) suspend fun addUserBook(userBook: UserBookEntity): Long
    @Update suspend fun updateUserBook(userBook: UserBookEntity)
    @Delete suspend fun deleteUserBook(userBook: UserBookEntity)
    @Query ("select * from UserBooks") fun getUserBooks(): Flow<List<UserBookEntity>>
    @Query ("select * from UserBooks WHERE userid = :userId") fun getUserBooksByUser(userId: Long): Flow<List<UserBookEntity>>
}