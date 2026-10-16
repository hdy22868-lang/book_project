package com.example.bookproject1.book_feature.presentation.settings.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookproject1.book_feature.data.local.ThemeDataStore
import com.example.bookproject1.book_feature.data.local.dao.BookDao
import com.example.bookproject1.book_feature.data.local.entity.BookHighlightEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val themeDataStore: ThemeDataStore,
    private val bookDao: BookDao
) : ViewModel() {

    // 1. مراقبة حالة المظهر (داكن أو فاتح) المخزنة في الـ DataStore
    val isDarkTheme: StateFlow<Boolean> = themeDataStore.isDarkThemeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    // 2. مراقبة قائمة النصوص المفضلة والمظللة المحفوظة في قاعدة بيانات Room
    val highlights: StateFlow<List<BookHighlightEntity>> = bookDao.getAllHighlights()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // تغيير الثيم وحفظه في الـ DataStore فوراً
    fun onThemeChange(isDark: Boolean) {
        viewModelScope.launch {
            themeDataStore.setDarkTheme(isDark)
        }
    }

    // حذف نص من المفضلة
    fun deleteHighlight(id: Int) {
        viewModelScope.launch {
            bookDao.deleteHighlight(id)
        }
    }
}