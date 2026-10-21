package com.example.bookproject1.book_feature.presentation.search.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.bookproject1.book_feature.presentation.search.logic.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit = onNavigateBack,
    onNavigateSettings: () -> Unit = {},
    onResultClick: (partNumber: Int, orderIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    SearchContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
        onNavigateHome = onNavigateHome,
        onNavigateSettings = onNavigateSettings,
        onResultClick = onResultClick,
        modifier = modifier
    )
}

