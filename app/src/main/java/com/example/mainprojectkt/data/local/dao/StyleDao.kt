package com.example.mainprojectkt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mainprojectkt.data.local.entity.StyleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StyleDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE) suspend fun addStyle(style: StyleEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun addStyles(styles: List<StyleEntity>)
    @Update suspend fun updateStyle(style: StyleEntity)
    @Query("DELETE FROM Styles WHERE start = :start AND finish = :end")
    suspend fun deleteStyle(start: Int, end: Int)
    @Transaction
    suspend fun deleteStyles(diapasons: List<Pair<Int, Int>>) {
        diapasons.forEach { (start, end) ->
            deleteStyle(start, end)
        }
    }
    @Query ("select * from Styles") fun getStyles(): Flow<List<StyleEntity>>
    @Transaction
    @Query("SELECT * FROM Styles WHERE pageId = :pageId")
    suspend fun getStylesByPage(pageId: Long): List<StyleEntity>
}