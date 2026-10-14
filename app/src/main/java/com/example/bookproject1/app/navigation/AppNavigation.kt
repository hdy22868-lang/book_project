package com.example.bookproject1.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.bookproject1.book_feature.presentation.booktoc.logic.BookTocViewModel
import com.example.bookproject1.book_feature.presentation.booktoc.ui.BookTocScreen
import com.example.bookproject1.book_feature.presentation.home.logic.HomeViewModel
import com.example.bookproject1.book_feature.presentation.home.ui.HomeScreen
import com.example.bookproject1.book_feature.presentation.reading.logic.BookReadingViewModel
import com.example.bookproject1.book_feature.presentation.reading.ui.BookReadingScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeScreenRoute // الشاشة التي تفتح أولاً
    ) {

        // 1. الشاشة الرئيسية
        composable<HomeScreenRoute> {
            val viewModel = koinViewModel<HomeViewModel>()
            HomeScreen(
                viewModel = viewModel,
                onPartClick = { partId ->
                    // التنقل لشاشة الفهرس وتمرير رقم الجزء
                    navController.navigate(BookTocRoute(partId = partId))
                }
            )
        }

        // 2. شاشة الفهرس
        composable<BookTocRoute> { backStackEntry ->
            val viewModel = koinViewModel<BookTocViewModel>()
            // قراءة المسار الحالي لاستخراج partId
            val route = backStackEntry.toRoute<BookTocRoute>()

            BookTocScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() },
                onNodeClick = { startIndex, endIndex ->
                    // التنقل لشاشة القراءة وتمرير الإحداثيات
                    navController.navigate(
                        BookReadingRoute(
                            partId = route.partId,
                            startIndex = startIndex,
                            endIndex = endIndex
                        )
                    )
                }
            )
        }

        composable<BookReadingRoute> {
            val viewModel = koinViewModel<BookReadingViewModel>()
            BookReadingScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}