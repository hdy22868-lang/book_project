package com.example.bookproject1.book_feature.presentation.favorites.logic

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookproject1.app.navigation.BookReadingRoute
import com.example.bookproject1.book_feature.domain.model.BookHighlight
import com.example.bookproject1.book_feature.domain.repository.BookRepository
import com.example.bookproject1.book_feature.presentation.component.logic.BookPart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: BookRepository
) : ViewModel() {

    private val selectedPartFilter = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<FavoritesUiState> = combine(
        repository.getAllHighlights(),
        selectedPartFilter
    ) { highlights, partFilter ->
        FavoritesUiState(
            allHighlights = highlights,
            selectedPartFilter = partFilter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FavoritesUiState()
    )

    fun onFilterPartSelect(partNumber: Int?) {
        selectedPartFilter.value = partNumber
    }

    fun deleteHighlight(id: Int) {
        viewModelScope.launch {
            repository.deleteHighlight(id)
        }
    }
    fun openHighlightInBook(
        highlight: BookHighlight,
        onRouteReady: (BookReadingRoute) -> Unit
    ) {
        viewModelScope.launch {
            val range = repository.getSectionRangeForParagraph(highlight.paragraphId)
            val route = if (range != null) {
                BookReadingRoute(
                    partId = highlight.partNumber,
                    startIndex = range.startIndex,
                    endIndex = range.endIndex,
                    targetParagraphId = highlight.paragraphId
                )
            } else {
                BookReadingRoute(
                    partId = highlight.partNumber,
                    startIndex = 0,
                    endIndex = 1000,
                    targetParagraphId = highlight.paragraphId
                )
            }
            onRouteReady(route)
        }
    }

    suspend fun buildShareText(context: Context, highlight: BookHighlight): String {
        val paragraph = repository.getParagraphById(highlight.paragraphId)
        val partName = context.getString(BookPart.titleResOf(highlight.partNumber))

        val sourceDetails = buildString {
            appendLine("📖 المصدر: كتاب المصابيح")
            appendLine("• $partName")

            if (paragraph != null) {
                val mainTitle = if (!paragraph.mainSectionTitle.isNullOrBlank()) {
                    "${paragraph.mainSectionName}: ${paragraph.mainSectionTitle}"
                } else {
                    paragraph.mainSectionName
                }
                if (mainTitle.isNotBlank()) {
                    appendLine("• $mainTitle")
                }

                if (!paragraph.faslName.isNullOrBlank()) {
                    val fasl = if (!paragraph.faslTitle.isNullOrBlank()) {
                        "${paragraph.faslName}: ${paragraph.faslTitle}"
                    } else {
                        paragraph.faslName
                    }
                    appendLine("• $fasl")
                }

                if (!paragraph.mabhathName.isNullOrBlank()) {
                    val mabhath = if (!paragraph.mabhathTitle.isNullOrBlank()) {
                        "${paragraph.mabhathName}: ${paragraph.mabhathTitle}"
                    } else {
                        paragraph.mabhathName
                    }
                    appendLine("• $mabhath")
                }
            } else if (highlight.sectionTitle.isNotBlank()) {
                appendLine("• ${highlight.sectionTitle}")
            }
        }.trim()

        val appLink = "https://play.google.com/store/apps/details?id=${context.packageName}"

        return """
« ${highlight.selectedText.trim()} »

$sourceDetails

📲 للمطالعة والتحميل عبر تطبيق «المصابيح»:
$appLink
""".trimIndent()
    }
}