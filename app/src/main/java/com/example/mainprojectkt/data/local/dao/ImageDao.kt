package com.example.mainprojectkt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mainprojectkt.data.local.entity.ImageEntity

@Dao
interface ImageDao {
    @Insert
    suspend fun insertImage(image: ImageEntity)
    @Query("SELECT * FROM Images WHERE pageId = :pageId")
    suspend fun getImagesByPage(pageId: Long): List<ImageEntity>
}