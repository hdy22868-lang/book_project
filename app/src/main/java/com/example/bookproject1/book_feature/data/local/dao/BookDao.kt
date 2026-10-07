package com.example.bookproject1.book_feature.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bookproject1.book_feature.data.local.entity.BookEntity
import com.example.bookproject1.book_feature.data.local.entity.TocItemTuple
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    // إدخال نصوص الكتاب (سنستخدمها مرة واحدة عند حقن الـ JSON لأول مرة)
    @Insert
    suspend fun insertParagraphs(paragraphs: List<BookEntity>)

    // جلب جزء كامل من الكتاب مرتباً حسب التسلسل لعرضه
    @Query("SELECT * FROM book_paragraphs WHERE partNumber = :partNumber ORDER BY orderIndex ASC")
    fun getBookPart(partNumber: Int): Flow<List<BookEntity>>

    // بحث سريع عبر جدول الـ FTS
    @Query("""
    SELECT * FROM book_paragraphs
    WHERE id IN (
        SELECT rowid FROM book_paragraphs_fts
        WHERE book_paragraphs_fts MATCH :ftsQuery
    )
    ORDER BY partNumber, orderIndex
    LIMIT :limit
""")
    suspend fun search(ftsQuery: String, limit: Int): List<BookEntity>

    // فحص ما إذا كان الجزء موجود مسبقاً لمنع قراءة الـ JSON مرتين
    @Query("SELECT COUNT(*) FROM book_paragraphs WHERE partNumber = :partNumber")
    suspend fun getParagraphCountForPart(partNumber: Int): Int

    // جلب محتوى "باب" كامل بكل فصوله ومباحثه
    @Query("SELECT * FROM book_paragraphs WHERE partNumber = :partNumber AND mainSectionName = :babName ORDER BY orderIndex ASC")
    fun getBabContent(partNumber: Int, babName: String): Flow<List<BookEntity>>

    // جلب محتوى "فصل" محدد داخل باب معين
    @Query("SELECT * FROM book_paragraphs WHERE partNumber = :partNumber AND mainSectionName = :babName AND faslName = :faslName ORDER BY orderIndex ASC")
    fun getFaslContent(partNumber: Int, babName: String, faslName: String): Flow<List<BookEntity>>

    // جلب محتوى "مبحث" محدد جداً
    @Query("SELECT * FROM book_paragraphs WHERE partNumber = :partNumber AND mainSectionName = :babName AND faslName = :faslName AND mabhathName = :mabhathName ORDER BY orderIndex ASC")
    fun getMabhathContent(partNumber: Int, babName: String, faslName: String, mabhathName: String): Flow<List<BookEntity>>

    @Query("""
    SELECT mainSectionName, mainSectionTitle, faslName, faslTitle, mabhathName, mabhathTitle
    FROM book_paragraphs
    WHERE partNumber = :partNumber
    GROUP BY mainSectionName, faslName, mabhathName
    ORDER BY MIN(orderIndex) ASC
""")
    suspend fun getTableOfContents(partNumber: Int): List<TocItemTuple>
}