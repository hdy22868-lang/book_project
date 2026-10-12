package com.example.bookproject1.book_feature.data.repository

import android.content.Context
import com.example.bookproject1.book_feature.data.local.dao.BookDao
import com.example.bookproject1.book_feature.data.mapper.toDomainModel
import com.example.bookproject1.book_feature.data.mapper.toEntityList
import com.example.bookproject1.book_feature.data.remote.dto.BookTreeDto
import com.example.bookproject1.book_feature.data.util.ArabicNormalizer
import com.example.bookproject1.book_feature.domain.model.BookParagraph
import com.example.bookproject1.book_feature.domain.model.TocNode
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

    override suspend fun getTablOfContents(partNumber: Int): List<TocNode> {
        // 1. نجلب البيانات المسطحة من قاعدة البيانات (Room)
        val flatList = bookDao.getTableOfContents(partNumber)

        val tree = mutableListOf<TocNode>()

        // 2. تجميع البيانات حسب الباب (mainSectionTitle)
        val groupedByMain = flatList.groupBy { it.mainSectionTitle }

        for ((mainTitle, mainItems) in groupedByMain) {
            val validMainTitle = mainTitle?.takeIf { it.isNotEmpty() } ?: "بدون عنوان"

            // فحص: هل يوجد فصول داخل هذا الباب؟
            val validFasls = mainItems.filter { !it.faslTitle.isNullOrEmpty() }

            if (validFasls.isEmpty()) {
                // إذا لم توجد فصول، (مثل: المقدمة أو سيرة الإمام)، فهذا الباب يعتبر ورقة نهائية (Leaf)
                val firstItem = mainItems.first()
                val leafTitle = firstItem.mabhathTitle?.takeIf { it.isNotEmpty() } ?: validMainTitle
                tree.add(
                    TocNode.Leaf(
                        title = leafTitle,
                        startIndex = firstItem.startIndex,
                        endIndex = firstItem.endIndex
                    )
                )
            } else {
                // إذا وجدت فصول، إذن هذا الباب هو "فرع" (Branch) يحتوي على أبناء
                val faslNodes = mutableListOf<TocNode>()
                val groupedByFasl = mainItems.groupBy { it.faslTitle }

                for ((faslTitle, faslItems) in groupedByFasl) {
                    if (faslTitle.isNullOrEmpty()) {
                        // إذا كان هناك مباحث تابعة للباب مباشرة دون فصل
                        faslItems.forEach { item ->
                            val t = item.mabhathTitle?.takeIf { it.isNotEmpty() } ?: "بدون عنوان"
                            faslNodes.add(TocNode.Leaf(title = t, startIndex = item.startIndex, endIndex = item.endIndex))
                        }
                        continue
                    }

                    // فحص: هل يوجد مباحث داخل هذا الفصل؟
                    val validMabhaths = faslItems.filter { !it.mabhathTitle.isNullOrEmpty() }

                    if (validMabhaths.isEmpty()) {
                        // إذا كان الفصل لا يحتوي على مباحث، فهو ورقة نهائية (Leaf)
                        val first = faslItems.first()
                        faslNodes.add(TocNode.Leaf(title = faslTitle, startIndex = first.startIndex, endIndex = first.endIndex))
                    } else {
                        // إذا كان يحتوي على مباحث، نقوم بتحويلها إلى أوراق (Leaves)
                        val mabhathNodes = validMabhaths.map { mabhath ->
                            TocNode.Leaf(
                                title = mabhath.mabhathTitle!!,
                                startIndex = mabhath.startIndex,
                                endIndex = mabhath.endIndex
                            )
                        }
                        // نضيف الفصل كـ "فرع" (Branch) داخله المباحث
                        faslNodes.add(TocNode.Branch(title = faslTitle, children = mabhathNodes))
                    }
                }
                // أخيرًا، نضيف الباب كـ "فرع" رئيسي داخله الفصول
                tree.add(TocNode.Branch(title = validMainTitle, children = faslNodes))
            }
        }

        return tree
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

    override suspend fun getSectionContent(
        partNumber: Int, startIndex: Int, endIndex: Int
    ): List<BookParagraph> = withContext(Dispatchers.Default) {
        bookDao.getRange(partNumber, startIndex, endIndex).map { it.toDomainModel() }
    }

    private fun buildFtsQuery(raw: String): String? {
        val words = ArabicNormalizer.normalize(raw)
            .split(Regex("[^\\p{L}\\p{N}]+"))
            .filter { it.isNotBlank() }
        if (words.isEmpty()) return null
        return words.joinToString(" ") { "$it*" }
    }
}