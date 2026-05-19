package com.example.mainprojectkt.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.mainprojectkt.data.local.entity.BookEntity
import com.example.mainprojectkt.data.local.entity.PageEntity

data class BookWithPages (
    @Embedded
    val book: BookEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "bookId"
    )
    val pages: List<PageEntity>
)