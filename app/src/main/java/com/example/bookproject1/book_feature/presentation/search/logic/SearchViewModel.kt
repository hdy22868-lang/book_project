package com.example.bookproject1.book_feature.presentation.search.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookproject1.book_feature.domain.repository.BookRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            try {
                for (part in 1..5) {
                    repository.seedDatabaseIfEmpty(part)
                }
            } catch (e: Exception) {
                // تجاهل أي خطأ عابر
            }
        }
    }

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.OnQueryChange -> {
                val newQuery = action.newQuery
                _uiState.update { it.copy(query = newQuery) }
                debounceSearch(newQuery)
            }
            is SearchAction.OnFilterPartSelect -> {
                _uiState.update { state ->
                    val filtered = if (action.partNumber == null) {
                        state.results
                    } else {
                        state.results.filter { it.partNumber == action.partNumber }
                    }
                    state.copy(
                        selectedPartFilter = action.partNumber,
                        filteredResults = filtered
                    )
                }
            }
            SearchAction.OnClearQuery -> {
                searchJob?.cancel()
                _uiState.update {
                    it.copy(
                        query = "",
                        results = emptyList(),
                        filteredResults = emptyList(),
                        isLoading = false,
                        isSearched = false,
                        errorMessage = null
                    )
                }
            }
            SearchAction.OnSearchSubmit -> {
                searchJob?.cancel()
                executeSearch(_uiState.value.query)
            }
        }
    }

    private fun debounceSearch(query: String) {
        searchJob?.cancel()
        val trimmed = query.trim()
        if (trimmed.length < 2) {
            _uiState.update {
                it.copy(
                    results = emptyList(),
                    filteredResults = emptyList(),
                    isLoading = false,
                    isSearched = false
                )
            }
            return
        }

        searchJob = viewModelScope.launch {
            delay(350) // تأخير بسيط 350ms لانتظار انتهاء الكتابة
            executeSearch(trimmed)
        }
    }

    private fun executeSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return

        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val rawResults = repository.searchBook(trimmed)
                _uiState.update { state ->
                    val filtered = if (state.selectedPartFilter == null) {
                        rawResults
                    } else {
                        rawResults.filter { it.partNumber == state.selectedPartFilter }
                    }
                    state.copy(
                        results = rawResults,
                        filteredResults = filtered,
                        isLoading = false,
                        isSearched = true
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "حدث خطأ أثناء البحث، يرجى المحاولة ثانية.",
                        isSearched = true
                    )
                }
            }
        }
    }
}