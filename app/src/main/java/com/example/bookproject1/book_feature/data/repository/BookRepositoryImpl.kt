package com.example.bookproject1.book_feature.data.repository

import android.content.Context
import com.example.bookproject1.book_feature.data.local.dao.BookDao
import com.example.bookproject1.book_feature.data.mapper.toDomainModel
import com.example.bookproject1.book_feature.data.mapper.toEntityList
import com.example.bookproject1.book_feature.data.remote.dto.BookTreeDto
import com.example.bookproject1.book_feature.data.util.ArabicNormalizer
import com.example.bookproject1.book_feature.domain.model.BookParagraph
import com.example.bookproject1.book_feature.domain.model.TocItem
import com.example.bookproject1.book_feature.domain.repository.BookRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class BookRepositoryImpl(
    private val bookDao: BookDao,
    private val context: Context, // نحتاجه حتى نوصل لمجلد assets ونقرأ الملف
    private val gson: Gson        // مكتبة تحويل النصوص إلى كلاسات
) : BookRepository {

    private val seedMutex = Mutex()

    override suspend fun seedDatabaseIfEmpty(partNumber: Int) = withContext(Dispatchers.IO) {
        seedMutex.withLock {
            if (bookDao.getParagraphCountForPart(partNumber) > 0) return@withLock
            val dto = context.assets.open("part${partNumber}_tree.json")
                .bufferedReader()
                .use { gson.fromJson(it, BookTreeDto::class.java) }  // بدون String وسيط
            bookDao.insertParagraphs(dto.toEntityList(partNumber))
        }
    }

    override suspend fun getTableOfContents(partNumber: Int): List<TocItem> {
        // نجيب العناوين من القاعدة (Entity) ونحولها إلى شكل الواجهات (Domain)
        return bookDao.getTableOfContents(partNumber).map { it.toDomainModel() }
    }

    override fun getBabContent(partNumber: Int, babName: String): Flow<List<BookParagraph>> {
        return bookDao.getBabContent(partNumber, babName)
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.Default)
    }

    override fun getFaslContent(
        partNumber: Int,
        babName: String,
        faslName: String
    ): Flow<List<BookParagraph>> {
        return bookDao.getFaslContent(partNumber, babName, faslName)
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.Default)
    }

    override fun getMabhathContent(
        partNumber: Int,
        babName: String,
        faslName: String,
        mabhathName: String
    ): Flow<List<BookParagraph>> {
        return bookDao.getMabhathContent(partNumber, babName, faslName, mabhathName)
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.Default)
    }

    override suspend fun searchBook(query: String): List<BookParagraph> {
        val ftsQuery = buildFtsQuery(query) ?: return emptyList()
        return bookDao.search(ftsQuery, limit = 50).map { it.toDomainModel() }
    }

    private fun buildFtsQuery(raw: String): String? {
        val words = ArabicNormalizer.normalize(raw)
            .split(Regex("[^\\p{L}\\p{N}]+"))
            .filter { it.isNotBlank() }
        if (words.isEmpty()) return null
        return words.joinToString(" ") { "$it*" }
    }
}