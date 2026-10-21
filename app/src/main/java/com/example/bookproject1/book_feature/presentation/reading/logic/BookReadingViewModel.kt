package com.example.bookproject1.book_feature.presentation.reading.logic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.bookproject1.app.navigation.BookReadingRoute
import com.example.bookproject1.book_feature.domain.model.BookHighlight
import com.example.bookproject1.book_feature.domain.model.BookParagraph
import com.example.bookproject1.book_feature.domain.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookReadingViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: BookRepository
) : ViewModel() {
    private val route = savedStateHandle.toRoute<BookReadingRoute>()
    private val _uiState = MutableStateFlow(BookReadingUiState())
    val uiState: StateFlow<BookReadingUiState> = _uiState.asStateFlow()

    init {
        loadContent()
    }

    fun loadContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false) }
            try {
                val list = repository.getSectionContent(
                    partNumber = route.partId,
                    startIndex = route.startIndex,
                    endIndex = route.endIndex
                )

                val headerTitle = resolveTitle(list)
                val headerSubtitle = resolveSubtitle(list)

                _uiState.update {
                    it.copy(
                        paragraphs = list,
                        title = headerTitle,
                        subtitle = headerSubtitle,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, isError = true) }
            }
        }
    }

    private fun resolveTitle(list: List<BookParagraph>): String {
        val first = list.firstOrNull() ?: return ""
        return when {
            !first.mabhathTitle.isNullOrBlank() -> "${first.mabhathName ?: ""}: ${first.mabhathTitle}"
            !first.mabhathName.isNullOrBlank() -> first.mabhathName
            !first.faslTitle.isNullOrBlank() -> "${first.faslName ?: ""}: ${first.faslTitle}"
            !first.mainSectionTitle.isNullOrBlank() -> "${first.mainSectionName}: ${first.mainSectionTitle}"
            else -> first.mainSectionName
        }
    }

    private fun resolveSubtitle(list: List<BookParagraph>): String? {
        val first = list.firstOrNull() ?: return null
        val parts = mutableListOf<String>()
        if (first.mainSectionName.isNotBlank() && first.mabhathName != null) {
            parts.add(first.mainSectionName)
        }
        if (!first.faslName.isNullOrBlank() && first.mabhathName != null) {
            parts.add(first.faslName)
        }
        return if (parts.isNotEmpty()) parts.joinToString(" • ") else null
    }

    fun saveHighlightWithColor(selectedText: String, paragraph: BookParagraph, colorHex: Long) {
        viewModelScope.launch {
            val highlight = BookHighlight(
                partNumber = paragraph.partNumber,
                paragraphId = paragraph.id,
                sectionTitle = _uiState.value.title,
                selectedText = selectedText,
                colorHex = colorHex
            )
            repository.saveHighlight(highlight)
        }
    }
}