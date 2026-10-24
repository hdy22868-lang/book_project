package com.example.bookproject1.book_feature.presentation.search.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.bookproject1.app.navigation.BookReadingRoute
import com.example.bookproject1.book_feature.presentation.component.ui.BottomTab
import com.example.bookproject1.book_feature.presentation.search.logic.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onTabSelected: (BottomTab) -> Unit,
    onNavigateToReading: (BookReadingRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    SearchContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
        onTabSelected = onTabSelected,
        onResultClick = { paragraph ->
            viewModel.openSearchResultInBook(paragraph) { readingRoute ->
                onNavigateToReading(readingRoute)
            }
        },
        modifier = modifier
    )
}