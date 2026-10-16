package com.example.bookproject1.book_feature.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bookproject1.book_feature.data.local.entity.BookEntity
import com.example.bookproject1.book_feature.data.local.entity.BookHighlightEntity
import com.example.bookproject1.book_feature.data.local.entity.TocItemTuple
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    // 1. إدخال نصوص الكتاب عند شحن الـ JSON لأول مرة
    @Insert
    suspend fun insertParagraphs(paragraphs: List<BookEntity>)

    // 2. فحص ما إذا كان الجزء موجوداً مسبقاً لمنع التكرار
    @Query("SELECT COUNT(*) FROM book_paragraphs WHERE partNumber = :partNumber")
    suspend fun getParagraphCountForPart(partNumber: Int): Int

    // 3. جلب نصوص جزء كامل مرتبة بالتسلسل
    @Query("SELECT * FROM book_paragraphs WHERE partNumber = :partNumber ORDER BY orderIndex ASC")
    fun getBookPart(partNumber: Int): Flow<List<BookEntity>>

    // 4. جلب فهرس العناوين (الأبواب، الفصول، المباحث) مع إحداثيات البداية والنهاية
    @Query("""
    SELECT mainSectionName, mainSectionTitle, faslName, faslTitle, mabhathName, mabhathTitle,
           MIN(orderIndex) AS startIndex, MAX(orderIndex) AS endIndex
    FROM book_paragraphs
    WHERE partNumber = :partNumber
    GROUP BY mainSectionName, faslName, mabhathName
    ORDER BY MIN(orderIndex) ASC
""")
    suspend fun getTableOfContents(partNumber: Int): List<TocItemTuple>

    // 5. جلب نصوص أي قسم/مبحث/فصل بدقة متناهية حسب تسلسل البداية والنهاية
    @Query("""
    SELECT * FROM book_paragraphs
    WHERE partNumber = :partNumber AND orderIndex BETWEEN :startIndex AND :endIndex
    ORDER BY orderIndex
""")
    suspend fun getRange(partNumber: Int, startIndex: Int, endIndex: Int): List<BookEntity>

    // 6. بحث سريع ودقيق عبر جدول FTS مع دعم الفلترة برقم الجزء
    @Query("""
    SELECT * FROM book_paragraphs
    WHERE (:partNumber IS NULL OR partNumber = :partNumber)
      AND id IN (
        SELECT rowid FROM book_paragraphs_fts
        WHERE book_paragraphs_fts MATCH :ftsQuery
    )
    ORDER BY partNumber ASC, orderIndex ASC
    LIMIT :limit
""")
    suspend fun search(
        ftsQuery: String,
        partNumber: Int? = null,
        limit: Int = 300
    ): List<BookEntity>

    // 7. إدارة التظليلات والمفضلة
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighlight(highlight: BookHighlightEntity)

    @Query("SELECT * FROM favorite_highlights ORDER BY createdAt DESC")
    fun getAllHighlights(): Flow<List<BookHighlightEntity>>

    @Query("DELETE FROM favorite_highlights WHERE id = :id")
    suspend fun deleteHighlight(id: Int)
}