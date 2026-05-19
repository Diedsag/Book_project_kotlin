package com.example.mainprojectkt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mainprojectkt.data.local.entity.PageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PageDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE) suspend fun addPage(page: PageEntity)
    @Update suspend fun updatePage(page: PageEntity)
    @Delete suspend fun deletePage(page: PageEntity)
    @Query ("select * from Pages") fun getPages(): Flow<List<PageEntity>>
}