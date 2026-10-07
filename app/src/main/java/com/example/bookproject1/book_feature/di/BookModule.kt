package com.example.bookproject1.book_feature.di

import androidx.room.Room
import com.example.bookproject1.book_feature.data.local.BookDatabase
import com.example.bookproject1.book_feature.data.repository.BookRepositoryImpl
import com.example.bookproject1.book_feature.domain.repository.BookRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val bookModule = module {

    // 1. توفير مكتبة Gson بالطريقة الحديثة
    singleOf(::Gson)

    // 2. توفير الـ Repository وربطه بالـ Interface باستخدام bind
    singleOf(::BookRepositoryImpl) bind BookRepository::class

    // 3. بناء وتوفير قاعدة بيانات Room (تبقى single لأنها تستخدم Builder)
    single {
        Room.databaseBuilder(
            androidContext(),
            BookDatabase::class.java,
            "Almodheef_Book_Database"
        ).build()
    }

    // 4. توفير الـ DAO (تعتمد على قاعدة البيانات المبنية أعلاه)
    single {
        get<BookDatabase>().bookDao
    }
}