package com.example.bookproject1.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bookproject1.app.navigation.AppNavigation
import com.example.bookproject1.app.ui.theme.AlMasabeehTheme
import com.example.bookproject1.book_feature.data.local.ThemeDataStore
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val themeDataStore: ThemeDataStore by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // مراقبة الثيم المختار من DataStore وتطبيقه على كل شاشات التطبيق
            val isDarkTheme by themeDataStore.isDarkThemeFlow.collectAsState(initial = true)

            AlMasabeehTheme(darkTheme = isDarkTheme) {
                AppNavigation()
            }
        }
    }
}
