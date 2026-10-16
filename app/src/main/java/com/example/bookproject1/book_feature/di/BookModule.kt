package com.example.bookproject1.book_feature.di

import androidx.room.Room
import com.example.bookproject1.book_feature.data.local.BookDatabase
import com.example.bookproject1.book_feature.data.local.ThemeDataStore
import com.example.bookproject1.book_feature.data.repository.BookRepositoryImpl
import com.example.bookproject1.book_feature.domain.repository.BookRepository
import com.example.bookproject1.book_feature.presentation.booktoc.logic.BookTocViewModel
import com.example.bookproject1.book_feature.presentation.home.logic.HomeViewModel
import com.example.bookproject1.book_feature.presentation.reading.logic.BookReadingViewModel
import com.example.bookproject1.book_feature.presentation.search.logic.SearchViewModel
import com.example.bookproject1.book_feature.presentation.settings.logic.SettingsViewModel
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val bookModule = module {

    single { ThemeDataStore(androidContext()) }
    // 1. توفير مكتبة Gson بالطريقة الحديثة
    singleOf(::Gson)

    // 2. توفير الـ Repository وربطه بالـ Interface باستخدام bind
    singleOf(::BookRepositoryImpl) bind BookRepository::class

    // 3. بناء وتوفير قاعدة بيانات Room (تبقى single لأنها تستخدم Builder)
    single {
        Room.databaseBuilder(
            androidContext(),
            BookDatabase::class.java,
            "Almasabeeh_Book_Database"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    // 4. توفير الـ DAO (تعتمد على قاعدة البيانات المبنية أعلاه)
    single {
        get<BookDatabase>().bookDao
    }

    viewModelOf(::HomeViewModel)
    viewModelOf(::BookTocViewModel)
    viewModelOf(::BookReadingViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::SettingsViewModel)
}