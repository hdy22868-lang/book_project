package com.example.bookproject1.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.bookproject1.app.SplashScreen
import com.example.bookproject1.book_feature.presentation.booktoc.logic.BookTocViewModel
import com.example.bookproject1.book_feature.presentation.booktoc.ui.BookTocScreen
import com.example.bookproject1.book_feature.presentation.home.logic.HomeViewModel
import com.example.bookproject1.book_feature.presentation.home.ui.HomeScreen
import com.example.bookproject1.book_feature.presentation.search.logic.SearchViewModel
import com.example.bookproject1.book_feature.presentation.search.ui.SearchScreen
import com.example.bookproject1.book_feature.presentation.settings.logic.SettingsViewModel
import com.example.bookproject1.book_feature.presentation.settings.ui.FavoriteDetailScreen
import com.example.bookproject1.book_feature.presentation.settings.ui.SettingsScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var isSplashFinished by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // الشاشة الرئيسية وكافة وجهات التطبيق
        NavHost(
            navController = navController,
            startDestination = HomeScreenRoute
        ) {
            // 1. الشاشة الرئيسية
            composable<HomeScreenRoute> {
                val viewModel = koinViewModel<HomeViewModel>()
                HomeScreen(
                    viewModel = viewModel,
                    onPartClick = { partId ->
                        navController.navigate(BookTocRoute(partId = partId))
                    },
                    onSearchClick = {
                        navController.navigate(SearchRoute)
                    },
                    onSettingsClick = {
                        navController.navigate(SettingsRoute)
                    }
                )
            }

            // 2. شاشة الفهرس
            composable<BookTocRoute> { backStackEntry ->
                val viewModel = koinViewModel<BookTocViewModel>()
                val route = backStackEntry.toRoute<BookTocRoute>()

                BookTocScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() },
                    onNodeClick = { startIndex, endIndex ->
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

            // 3. شاشة البحث
            composable<SearchRoute> {
                val viewModel = koinViewModel<SearchViewModel>()
                SearchScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateHome = {
                        navController.navigate(HomeScreenRoute) {
                            popUpTo(HomeScreenRoute) { inclusive = true }
                        }
                    },
                    onNavigateSettings = {
                        navController.navigate(SettingsRoute)
                    },
                    onResultClick = { partNumber, orderIndex ->
                        navController.navigate(
                            BookReadingRoute(
                                partId = partNumber,
                                startIndex = orderIndex,
                                endIndex = orderIndex
                            )
                        )
                    }
                )
            }

            // 4. شاشة الإعدادات
            composable<SettingsRoute> {
                val viewModel = koinViewModel<SettingsViewModel>()
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateHome = {
                        navController.navigate(HomeScreenRoute) {
                            popUpTo(HomeScreenRoute) { inclusive = true }
                        }
                    },
                    onNavigateSearch = {
                        navController.navigate(SearchRoute)
                    },
                    onHighlightClick = { highlightId ->
                        navController.navigate(FavoriteDetailRoute(highlightId = highlightId))
                    }
                )
            }

            // 5. شاشة تفاصيل النص المفضل
            composable<FavoriteDetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<FavoriteDetailRoute>()
                val viewModel = koinViewModel<SettingsViewModel>()
                FavoriteDetailScreen(
                    highlightId = route.highlightId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }

        // شاشة البداية تكون كطبقة فوق الشاشة الرئيسية، وتذوب تدريجياً لتكشف الرئيسية تحتها
        if (!isSplashFinished) {
            SplashScreen(
                onSplashFinished = { isSplashFinished = true }
            )
        }
    }
}