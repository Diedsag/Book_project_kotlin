package com.example.mainprojectkt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mainprojectkt.data.local.entity.ImageEntity

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<ImageEntity>): List<Long>

    @Query("SELECT * FROM Images WHERE pageId = :pageId ORDER BY position ASC")
    suspend fun getImagesByPage(pageId: Long): List<ImageEntity>

    @Query("DELETE FROM Images WHERE pageId = :pageId")
    suspend fun deleteImagesByPage(pageId: Long)
}