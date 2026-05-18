package com.example.mainprojectkt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mainprojectkt.data.local.entity.StyleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StyleDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE) suspend fun addStyle(todo: StyleEntity)
    @Update suspend fun updateStyle(todo: StyleEntity)
    @Delete suspend fun deleteStyle(todo: StyleEntity)
    @Query ("select * from Styles") fun getStyles(): Flow<List<StyleEntity>>
}