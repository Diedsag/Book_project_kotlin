package com.example.mainprojectkt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mainprojectkt.data.local.entity.PageEntity
import com.example.mainprojectkt.data.model.BookWithPages
import kotlinx.coroutines.flow.Flow

@Dao
interface PageDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE) suspend fun addPage(page: PageEntity): Long
    @Update suspend fun updatePage(page: PageEntity)
    @Delete suspend fun deletePage(page: PageEntity)
    @Query ("select * from Pages") fun getPages(): Flow<List<PageEntity>>

    @Transaction
    @Query("SELECT * FROM Pages WHERE bookId = :bookId")
    suspend fun getPagesByBook(bookId: Long): List<PageEntity>
}