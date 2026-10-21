package com.example.bookproject1.book_feature.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class ThemeDataStore(private val context: Context) {

    companion object {
        val KEY_IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    }

    val isDarkThemeFlow: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[KEY_IS_DARK_THEME] ?: true
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_IS_DARK_THEME] = isDark
        }
    }
}