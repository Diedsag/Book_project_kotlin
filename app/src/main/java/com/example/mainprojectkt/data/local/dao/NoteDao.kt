package com.example.mainprojectkt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mainprojectkt.data.local.entity.BookEntity
import com.example.mainprojectkt.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE) suspend fun addNote(note: NoteEntity): Long
    @Query ("select * from Notes") fun getNotes(): Flow<List<NoteEntity>>
    @Query("delete from Notes where id = :id")
    suspend fun deleteNote(id: Long)
}