package com.example.bookproject1.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bookproject1.app.navigation.AppNavigation
import com.example.bookproject1.app.ui.theme.AlMasabeehTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlMasabeehTheme() {
                AppNavigation()
            }
        }
    }
}
