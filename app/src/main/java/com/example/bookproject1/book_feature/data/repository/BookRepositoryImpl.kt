package com.example.bookproject1.book_feature.data.repository

import android.content.Context
import com.example.bookproject1.book_feature.data.local.dao.BookDao
import com.example.bookproject1.book_feature.data.mapper.toDomainModel
import com.example.bookproject1.book_feature.data.mapper.toEntityList
import com.example.bookproject1.book_feature.data.remote.dto.BookTreeDto
import com.example.bookproject1.book_feature.domain.model.BookParagraph
import com.example.bookproject1.book_feature.domain.model.TocItem
import com.example.bookproject1.book_feature.domain.repository.BookRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookRepositoryImpl(
    private val bookDao: BookDao,
    private val context: Context, // نحتاجه حتى نوصل لمجلد assets ونقرأ الملف
    private val gson: Gson        // مكتبة تحويل النصوص إلى كلاسات
) : BookRepository {

    override suspend fun seedDatabaseIfEmpty(partNumber: Int) {
        // 1. نسأل قاعدة البيانات: هل هذا الجزء موجود ومقروء مسبقاً؟
        val count = bookDao.getParagraphCountForPart(partNumber)

        // إذا كان العدد صفر (0)، يعني التطبيق توه نازل أو المستخدم مسح البيانات
        if (count == 0) {
            // 2. نجيب اسم الملف بناءً على رقم الجزء (مثلاً part1_tree.json)
            val fileName = "part${partNumber}_tree.json"

            // 3. نفتح مجلد assets ونقرأ كل النص الموجود داخل الملف
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }

            // 4. ننطي النص لمكتبة Gson حتى تفرغه بداخل قالب الـ DTO (الشجرة)
            val bookTreeDto = gson.fromJson(jsonString, BookTreeDto::class.java)

            // 5. نستخدم المابر (الجسر) اللي سويناه حتى نفلش الشجرة ونحولها إلى أسطر مسطحة
            val entities = bookTreeDto.toEntityList(partNumber)

            // 6. أخيراً، نحفظ كل هاي الأسطر بضربة وحدة بداخل قاعدة بيانات Room
            bookDao.insertParagraphs(entities)
        }
    }

    override suspend fun getTableOfContents(partNumber: Int): List<TocItem> {
        // نجيب العناوين من القاعدة (Entity) ونحولها إلى شكل الواجهات (Domain)
        return bookDao.getTableOfContents(partNumber).map { it.toDomainModel() }
    }

    override fun getBabContent(partNumber: Int, babName: String): Flow<List<BookParagraph>> {
        // نستخدم flow.map لتحويل كل قائمة تجينا من القاعدة إلى قائمة تقرأها الواجهات
        return bookDao.getBabContent(partNumber, babName).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun getFaslContent(
        partNumber: Int,
        babName: String,
        faslName: String
    ): Flow<List<BookParagraph>> {
        return bookDao.getFaslContent(partNumber, babName, faslName).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun getMabhathContent(
        partNumber: Int,
        babName: String,
        faslName: String,
        mabhathName: String
    ): Flow<List<BookParagraph>> {
        return bookDao.getMabhathContent(partNumber, babName, faslName, mabhathName).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun searchBook(query: String): List<BookParagraph> {
        return bookDao.searchContent(query).map { it.toDomainModel() }
    }
}