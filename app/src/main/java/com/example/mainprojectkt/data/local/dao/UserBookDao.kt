package com.example.mainprojectkt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mainprojectkt.data.local.entity.UserBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserBookDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE) suspend fun addUserBook(book: UserBookEntity): Long
    @Query("SELECT bookId FROM UserBooks WHERE userId = :userId")
    fun getBookIdsByUser(userId: Long): Flow<List<Long>>
}