package com.example.bookproject1.book_feature.presentation.booktoc.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bookproject1.book_feature.presentation.booktoc.logic.BookTocViewModel

@Composable
fun BookTocScreen(
    viewModel: BookTocViewModel,
    onNavigateBack: () -> Unit,
    onNodeClick: (startIndex: Int, endIndex: Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    BookTocContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onToggle = viewModel::onToggle,
        onRetry = viewModel::load,
        onNodeClick = onNodeClick
    )
}