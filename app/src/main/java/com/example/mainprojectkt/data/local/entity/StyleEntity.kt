package com.example.mainprojectkt.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.mainprojectkt.domain.model.TextStyleData

@Entity(
    tableName = "Styles",
    indices = [Index(value = ["pageId", "id"], unique = false)],
    foreignKeys = [
        ForeignKey(
            entity = PageEntity::class,
            parentColumns = ["id"],
            childColumns = ["pageId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StyleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val pageId: Long,
    val start: Int,
    val finish: Int,
    val link: String?,
    val originalStyle: TextStyleData? = null
)