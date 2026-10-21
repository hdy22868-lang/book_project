package com.example.bookproject1.book_feature.presentation.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.bookproject1.book_feature.presentation.home.logic.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onPartClick: (Int) -> Unit,
    onSettingsClick: () -> Unit = {},
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        onToggleLayout = viewModel::toggleLayout,
        onPartClick = onPartClick,
        onSettingsClick = onSettingsClick,
        onSearchClick = onSearchClick,
        modifier = modifier
    )
}