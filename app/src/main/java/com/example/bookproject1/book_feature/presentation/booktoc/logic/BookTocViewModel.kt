package com.example.bookproject1.book_feature.presentation.booktoc.logic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.bookproject1.app.navigation.BookTocRoute
import com.example.bookproject1.book_feature.domain.repository.BookRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookTocViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: BookRepository
) : ViewModel() {

    private val partId: Int = savedStateHandle.toRoute<BookTocRoute>().partId

    private val _uiState = MutableStateFlow(BookTocUiState(partNumber = partId))
    val uiState: StateFlow<BookTocUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false) }
            try {
                repository.seedDatabaseIfEmpty(partId)
                val nodes = repository.getTablOfContents(partId)
                _uiState.update { it.copy(nodes = nodes, isLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, isError = true) }
            }
        }
    }

    fun onToggle(id: String) {
        _uiState.update { state ->
            val expanded = state.expandedIds
            state.copy(expandedIds = if (id in expanded) expanded - id else expanded + id)
        }
    }
}