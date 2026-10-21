package com.example.bookproject1.book_feature.domain.repository

import com.example.bookproject1.book_feature.domain.model.BookHighlight
import com.example.bookproject1.book_feature.domain.model.BookParagraph
import com.example.bookproject1.book_feature.domain.model.TocNode
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    suspend fun seedDatabaseIfEmpty(partNumber: Int)

    suspend fun getTablOfContents(partNumber: Int): List<TocNode>

    suspend fun searchBook(query: String, partNumber: Int? = null): List<BookParagraph>

    suspend fun getSectionContent(partNumber: Int, startIndex: Int, endIndex: Int): List<BookParagraph>

    val isDarkThemeFlow: Flow<Boolean>

    suspend fun setDarkTheme(isDark: Boolean)

    fun getAllHighlights(): Flow<List<BookHighlight>>

    suspend fun saveHighlight(highlight: BookHighlight)

    suspend fun deleteHighlight(id: Int)
}