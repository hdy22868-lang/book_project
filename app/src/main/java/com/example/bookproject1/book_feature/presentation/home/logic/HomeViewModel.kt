package com.example.bookproject1.book_feature.presentation.home.logic

import androidx.lifecycle.ViewModel
import com.example.bookproject1.R
import com.example.bookproject1.book_feature.presentation.component.logic.BookPart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadParts()
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnToggleLayoutClick -> toggleLayout()
        }
    }

    private fun toggleLayout() {
        _uiState.update { currentState ->
            currentState.copy(isGridLayout = !currentState.isGridLayout)
        }
    }

    private fun loadParts() {
        _uiState.update { currentState ->
            currentState.copy(
                parts = listOf(
                    BookPart(
                        partNumber = 1,
                        titleRes = R.string.part_one,
                        coverRes = R.drawable.part1
                    ),
                    BookPart(
                        partNumber = 2,
                        titleRes = R.string.part_two,
                        coverRes = R.drawable.part2
                    ),
                    BookPart(
                        partNumber = 3,
                        titleRes = R.string.part_three,
                        coverRes = R.drawable.part3
                    ),
                    BookPart(
                        partNumber = 4,
                        titleRes = R.string.part_four,
                        coverRes = R.drawable.part4
                    ),
                    BookPart(
                        partNumber = 5,
                        titleRes = R.string.part_five,
                        coverRes = R.drawable.part5
                    )
                )
            )
        }
    }
}