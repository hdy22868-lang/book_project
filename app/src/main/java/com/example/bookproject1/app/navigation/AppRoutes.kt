package com.example.bookproject1.app.navigation

import kotlinx.serialization.Serializable

// مسار الشاشة الرئيسية (لا تحتاج لمتغيرات)
@Serializable
object HomeScreenRoute

// مسار شاشة الفهرس (تحتاج رقم الجزء)
@Serializable
data class BookTocRoute(val partId: Int)

// مسار شاشة القراءة (تحتاج رقم الجزء ونقطة بداية ونهاية النص)
@Serializable
data class BookReadingRoute(val partId: Int, val startIndex: Int, val endIndex: Int)
@Serializable
object SearchRoute
@Serializable
object SettingsRoute

@Serializable
data class FavoriteDetailRoute(val highlightId: Int)
// مسار شاشة البداية (Splash)
@Serializable
object SplashRoute