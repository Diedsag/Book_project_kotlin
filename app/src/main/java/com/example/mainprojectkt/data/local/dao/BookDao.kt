package com.example.mainprojectkt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mainprojectkt.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE) suspend fun addBook(todo: BookEntity)
    @Update suspend fun updateBook(todo: BookEntity)
    @Delete suspend fun deleteBook(todo: BookEntity)
    @Query ("select * from Books") fun getBooks(): Flow<List<BookEntity>>
}