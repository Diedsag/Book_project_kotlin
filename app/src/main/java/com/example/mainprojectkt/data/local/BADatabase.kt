package com.example.mainprojectkt.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mainprojectkt.data.local.dao.BookDao
import com.example.mainprojectkt.data.local.dao.NoteDao
import com.example.mainprojectkt.data.local.dao.PageDao
import com.example.mainprojectkt.data.local.dao.StyleDao
import com.example.mainprojectkt.data.local.dao.UserBookDao
import com.example.mainprojectkt.data.local.dao.UserDao
import com.example.mainprojectkt.data.local.entity.BookEntity
import com.example.mainprojectkt.data.local.entity.NoteEntity
import com.example.mainprojectkt.data.local.entity.PageEntity
import com.example.mainprojectkt.data.local.entity.StyleEntity
import com.example.mainprojectkt.data.local.entity.UserBookEntity
import com.example.mainprojectkt.data.local.entity.UserEntity

@Database(entities = [BookEntity::class, PageEntity::class, StyleEntity::class,
    UserEntity::class, UserBookEntity::class, NoteEntity::class]
    , version = 3, exportSchema = true)
abstract class BADatabase: RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun pageDao(): PageDao
    abstract fun styleDao(): StyleDao
    abstract fun userDao(): UserDao
    abstract fun userBookDao(): UserBookDao
    abstract fun noteDao(): NoteDao

    companion object{
        @Volatile private var instance: BADatabase? = null

        fun getDatabase(context: Context): BADatabase = instance ?: synchronized(this){
            Room.databaseBuilder(context, BADatabase::class.java, "main_database")
                .build().also { instance = it }
        }
    }
}