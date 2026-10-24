package com.example.bookproject1.book_feature.data.repository

import android.content.Context
import com.example.bookproject1.book_feature.data.local.datastore.ThemeDataStore
import com.example.bookproject1.book_feature.data.local.dao.BookDao
import com.example.bookproject1.book_feature.data.mapper.toDomainModel
import com.example.bookproject1.book_feature.data.mapper.toEntity
import com.example.bookproject1.book_feature.data.mapper.toEntityList
import com.example.bookproject1.book_feature.data.remote.dto.BookTreeDto
import com.example.bookproject1.book_feature.data.util.ArabicNormalizer
import com.example.bookproject1.book_feature.data.util.TocItemTuple
import com.example.bookproject1.book_feature.domain.model.BookHighlight
import com.example.bookproject1.book_feature.domain.model.BookParagraph
import com.example.bookproject1.book_feature.domain.model.TocNode
import com.example.bookproject1.book_feature.domain.repository.BookRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class BookRepositoryImpl(
    private val bookDao: BookDao,
    private val context: Context,
    private val gson: Gson,
    private val themeDataStore: ThemeDataStore
) : BookRepository {

    private val seedMutex = Mutex()

    override suspend fun seedDatabaseIfEmpty(partNumber: Int) = withContext(Dispatchers.IO) {
        seedMutex.withLock {
            if (bookDao.getParagraphCountForPart(partNumber) > 0) return@withLock
            val dto = context.assets.open("part${partNumber}_tree.json")
                .bufferedReader()
                .use { gson.fromJson(it, BookTreeDto::class.java) }
            bookDao.insertParagraphs(dto.toEntityList(partNumber))
        }
    }

    override suspend fun getTablOfContents(partNumber: Int): List<TocNode> {
        val flatList = bookDao.getTableOfContents(partNumber)
        val tree = mutableListOf<TocNode>()

        for ((babName, babRows) in flatList.groupBy { it.mainSectionName }) {
            val babTitle = babRows.first().mainSectionTitle
            val faslRows = babRows.filter { it.faslName != null }

            if (faslRows.isEmpty()) {
                tree.add(
                    TocNode.Leaf(
                        title = tocLabel(babName, babTitle),
                        startIndex = babRows.minOf { it.startIndex },
                        endIndex = babRows.maxOf { it.endIndex }
                    )
                )
                continue
            }

            val babChildren = mutableListOf<TocNode>()

            babRows.filter { it.faslName == null }.forEach { row ->
                babChildren.add(
                    TocNode.Leaf(
                        title = babTitle?.takeIf { it.isNotBlank() } ?: babName,
                        startIndex = row.startIndex,
                        endIndex = row.endIndex
                    )
                )
            }

            for ((faslName, rows) in faslRows.groupBy { it.faslName }) {
                val name = faslName ?: continue
                val faslLabel = tocLabel(name, rows.first().faslTitle)
                val mabhathRows = rows.filter { it.mabhathName != null }
                val directRow = rows.firstOrNull { it.mabhathName == null }

                if (mabhathRows.isEmpty()) {
                    babChildren.add(
                        TocNode.Leaf(
                            title = faslLabel,
                            startIndex = rows.minOf { it.startIndex },
                            endIndex = rows.maxOf { it.endIndex }
                        )
                    )
                } else {
                    val children = mutableListOf<TocNode>()
                    if (directRow != null) {
                        children.add(
                            TocNode.Leaf(
                                title = "نص الفصل",
                                startIndex = directRow.startIndex,
                                endIndex = directRow.endIndex
                            )
                        )
                    }
                    mabhathRows.forEach { m ->
                        children.add(
                            TocNode.Leaf(
                                title = tocLabel(m.mabhathName!!, m.mabhathTitle, spaceBeforeColon = false),
                                startIndex = m.startIndex,
                                endIndex = m.endIndex
                            )
                        )
                    }
                    babChildren.add(TocNode.Branch(title = faslLabel, children = children))
                }
            }

            tree.add(TocNode.Branch(title = tocLabel(babName, babTitle), children = babChildren))
        }

        return tree
    }

    override suspend fun searchBook(query: String, partNumber: Int?): List<BookParagraph> {
        val ftsQuery = buildFtsQuery(query) ?: return emptyList()
        return bookDao.search(ftsQuery, partNumber = partNumber, limit = 300).map { it.toDomainModel() }
    }

    override suspend fun getSectionContent(
        partNumber: Int, startIndex: Int, endIndex: Int
    ): List<BookParagraph> = withContext(Dispatchers.Default) {
        bookDao.getRange(partNumber, startIndex, endIndex).map { it.toDomainModel() }
    }

    override val isDarkThemeFlow: Flow<Boolean> = themeDataStore.isDarkThemeFlow

    override suspend fun setDarkTheme(isDark: Boolean) {
        themeDataStore.setDarkTheme(isDark)
    }

    override fun getAllHighlights(): Flow<List<BookHighlight>> =
        bookDao.getAllHighlights().map { list -> list.map { it.toDomainModel() } }

    override suspend fun saveHighlight(highlight: BookHighlight) {
        bookDao.insertHighlight(highlight.toEntity())
    }

    override suspend fun deleteHighlight(id: Int) {
        bookDao.deleteHighlight(id)
    }

    override suspend fun getSectionRangeForParagraph(paragraphId: Int): TocItemTuple? =
        withContext(Dispatchers.IO) {
            bookDao.getSectionRangeForParagraph(paragraphId)
        }

    override suspend fun getParagraphById(id: Int): BookParagraph? = withContext(Dispatchers.IO) {
        bookDao.getParagraphById(id)?.toDomainModel()
    }

    private fun tocLabel(name: String, title: String?, spaceBeforeColon: Boolean = true): String =
        when {
            title.isNullOrBlank() -> name
            spaceBeforeColon -> "$name : $title"
            else -> "$name: $title"
        }

    private fun buildFtsQuery(raw: String): String? {
        val words = ArabicNormalizer.normalize(raw)
            .split(Regex("[^\\p{L}\\p{N}]+"))
            .filter { it.isNotBlank() }
        if (words.isEmpty()) return null
        return words.joinToString(" ") { "$it*" }
    }
}