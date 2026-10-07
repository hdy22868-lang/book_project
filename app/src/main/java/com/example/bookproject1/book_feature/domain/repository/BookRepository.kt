package com.example.bookproject1.book_feature.domain.repository

import com.example.bookproject1.book_feature.domain.model.BookParagraph
import com.example.bookproject1.book_feature.domain.model.TocItem
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    // 1. فحص وحقن البيانات من ملف الـ JSON إلى قاعدة البيانات (تشتغل مرة وحدة بالبداية)
    suspend fun seedDatabaseIfEmpty(partNumber: Int)

    // 2. جلب محتوى الفهرس كامل (أبواب، فصول، ومباحث) لبناء الكارتات
    suspend fun getTableOfContents(partNumber: Int): List<TocItem>

    // 3. جلب محتوى "باب" كامل
    fun getBabContent(partNumber: Int, babName: String): Flow<List<BookParagraph>>

    // 4. جلب محتوى "فصل" محدد داخل باب معين
    fun getFaslContent(partNumber: Int, babName: String, faslName: String): Flow<List<BookParagraph>>

    // 5. جلب محتوى "مبحث" محدد جداً
    fun getMabhathContent(partNumber: Int, babName: String, faslName: String, mabhathName: String): Flow<List<BookParagraph>>

    // 6. البحث السريع في كل نصوص الكتاب
    suspend fun searchBook(query: String): List<BookParagraph>

    suspend fun getSectionContent(partNumber: Int, startIndex: Int, endIndex: Int): List<BookParagraph>
}