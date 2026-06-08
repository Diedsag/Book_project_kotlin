package com.example.mainprojectkt.data.local

import androidx.room.TypeConverter
import com.example.mainprojectkt.domain.model.TextStyleData
import org.json.JSONObject

class Converters {
    @TypeConverter
    fun fromTextStyleData(style: TextStyleData?): String? {
        if (style == null) return null
        return JSONObject().apply {
            put("size", style.size.toDouble())
            put("index", style.index)
            put("font", style.font)
            put("bold", style.bold)
            put("italic", style.italic)
            put("color", style.color.toLong() and 0xFFFFFFFFL)
            style.backgroundColor?.let {
                put("backgroundColor", it.toLong() and 0xFFFFFFFFL)
            }
        }.toString()
    }
    @TypeConverter
    fun toTextStyleData(json: String?): TextStyleData? {
        if (json == null) return null
        return try {
            val jsonObject = JSONObject(json)
            TextStyleData(
                size = jsonObject.getDouble("size").toFloat(),
                index = jsonObject.getString("index"),
                font = jsonObject.getString("font"),
                bold = jsonObject.getBoolean("bold"),
                italic = jsonObject.getBoolean("italic"),
                color = jsonObject.getLong("color").toInt(),
                backgroundColor = if (jsonObject.has("backgroundColor")) {
                    jsonObject.getLong("backgroundColor").toInt()
                } else null
            )
        } catch (e: Exception) {
            null
        }
    }
}