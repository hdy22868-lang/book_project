package com.example.bookproject1.book_feature.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bookproject1.book_feature.data.local.dao.BookDao
import com.example.bookproject1.book_feature.data.local.entity.BookEntity
import com.example.bookproject1.book_feature.data.local.entity.BookFtsEntity
import com.example.bookproject1.book_feature.data.local.entity.BookHighlightEntity

@Database(entities = [BookEntity::class, BookFtsEntity::class, BookHighlightEntity::class],
    version = 8,
    exportSchema = true)
abstract class BookDatabase : RoomDatabase() {

    // هذا السطر يخلينا نكدر نوصل لدوال الـ DAO من خلال قاعدة البيانات
    abstract val bookDao: BookDao
}