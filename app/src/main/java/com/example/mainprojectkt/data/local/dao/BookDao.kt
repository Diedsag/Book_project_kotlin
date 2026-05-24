package com.example.mainprojectkt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mainprojectkt.data.local.entity.BookEntity
import com.example.mainprojectkt.data.model.BookWithPages
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE) suspend fun addBook(book: BookEntity): Long
    @Update suspend fun updateBook(book: BookEntity)
    @Delete suspend fun deleteBook(book: BookEntity)
    @Query ("select * from Books") fun getBooks(): Flow<List<BookEntity>>

    @Transaction
    @Query("SELECT * FROM Books WHERE id = :bookId")
    suspend fun getBookWithPages(bookId: Long): BookWithPages?
}