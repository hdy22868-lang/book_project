package com.example.bookproject1.book_feature.presentation.favorites.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.bookproject1.book_feature.presentation.component.ui.BottomTab
import com.example.bookproject1.book_feature.presentation.favorites.logic.FavoritesViewModel

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onTabSelected: (BottomTab) -> Unit,
    onHighlightClick: (highlightId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    FavoritesContent(
        uiState = uiState,
        onFilterPartSelect = viewModel::onFilterPartSelect,
        onDeleteHighlight = viewModel::deleteHighlight,
        onHighlightClick = onHighlightClick,
        onTabSelected = onTabSelected,
        modifier = modifier
    )
}