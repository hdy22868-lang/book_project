package com.example.bookproject1.app

import android.app.Application
import com.example.bookproject1.book_feature.di.bookModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class BookApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BookApp)
            modules(bookModule)
        }
    }
}