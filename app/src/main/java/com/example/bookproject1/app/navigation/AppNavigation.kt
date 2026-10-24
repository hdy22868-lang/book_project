package com.example.bookproject1.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.bookproject1.app.SplashScreen
import com.example.bookproject1.book_feature.presentation.booktoc.logic.BookTocViewModel
import com.example.bookproject1.book_feature.presentation.booktoc.ui.BookTocScreen
import com.example.bookproject1.book_feature.presentation.component.ui.BottomTab
import com.example.bookproject1.book_feature.presentation.favorites.logic.FavoritesViewModel
import com.example.bookproject1.book_feature.presentation.favorites.ui.FavoriteDetailScreen
import com.example.bookproject1.book_feature.presentation.favorites.ui.FavoritesScreen
import com.example.bookproject1.book_feature.presentation.home.logic.HomeViewModel
import com.example.bookproject1.book_feature.presentation.home.ui.HomeScreen
import com.example.bookproject1.book_feature.presentation.reading.logic.BookReadingViewModel
import com.example.bookproject1.book_feature.presentation.reading.ui.BookReadingScreen
import com.example.bookproject1.book_feature.presentation.search.logic.SearchViewModel
import com.example.bookproject1.book_feature.presentation.search.ui.SearchScreen
import com.example.bookproject1.book_feature.presentation.settings.logic.SettingsViewModel
import com.example.bookproject1.book_feature.presentation.settings.ui.SettingsScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var isSplashFinished by remember { mutableStateOf(false) }


    val onBottomTabSelected: (BottomTab) -> Unit = { tab ->
        val targetRoute: Any = when (tab) {
            BottomTab.HOME -> HomeScreenRoute
            BottomTab.SEARCH -> SearchRoute
            BottomTab.FAVORITES -> FavoritesRoute
            BottomTab.SETTINGS -> SettingsRoute
        }
        navController.navigateToBottomTab(targetRoute)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = HomeScreenRoute
        ) {

            composable<HomeScreenRoute> {
                val viewModel = koinViewModel<HomeViewModel>()
                HomeScreen(
                    viewModel = viewModel,
                    onPartClick = { partId ->
                        navController.navigate(BookTocRoute(partId = partId))
                    },
                    onTabSelected = onBottomTabSelected
                )
            }


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


            composable<BookReadingRoute> {
                val viewModel = koinViewModel<BookReadingViewModel>()
                BookReadingScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }


            composable<SearchRoute> {
                val viewModel = koinViewModel<SearchViewModel>()
                SearchScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() },
                    onTabSelected = onBottomTabSelected,
                    onNavigateToReading = { readingRoute ->
                        navController.navigate(readingRoute)
                    }
                )
            }

            composable<FavoritesRoute> {
                val viewModel = koinViewModel<FavoritesViewModel>()
                FavoritesScreen(
                    viewModel = viewModel,
                    onTabSelected = onBottomTabSelected,
                    onHighlightClick = { highlightId ->
                        navController.navigate(FavoriteDetailRoute(highlightId = highlightId))
                    }
                )
            }


            composable<SettingsRoute> {
                val viewModel = koinViewModel<SettingsViewModel>()
                SettingsScreen(
                    viewModel = viewModel,
                    onTabSelected = onBottomTabSelected
                )
            }


            composable<FavoriteDetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<FavoriteDetailRoute>()
                val viewModel = koinViewModel<FavoritesViewModel>()
                FavoriteDetailScreen(
                    highlightId = route.highlightId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToReading = { readingRoute ->
                        navController.navigate(readingRoute)
                    }
                )
            }
        }

        if (!isSplashFinished) {
            SplashScreen(
                onSplashFinished = { isSplashFinished = true }
            )
        }
    }
}

private fun NavHostController.navigateToBottomTab(route: Any) {
    navigate(route) {
        popUpTo(HomeScreenRoute) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}