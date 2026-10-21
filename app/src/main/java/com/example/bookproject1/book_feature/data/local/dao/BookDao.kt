package com.example.bookproject1.book_feature.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bookproject1.book_feature.data.local.entity.BookEntity
import com.example.bookproject1.book_feature.data.local.entity.BookHighlightEntity
import com.example.bookproject1.book_feature.data.util.TocItemTuple
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert
    suspend fun insertParagraphs(paragraphs: List<BookEntity>)

    @Query("SELECT COUNT(*) FROM book_paragraphs WHERE partNumber = :partNumber")
    suspend fun getParagraphCountForPart(partNumber: Int): Int

    @Query("SELECT * FROM book_paragraphs WHERE partNumber = :partNumber ORDER BY orderIndex ASC")
    fun getBookPart(partNumber: Int): Flow<List<BookEntity>>

    @Query(
        """
    SELECT mainSectionName, mainSectionTitle, faslName, faslTitle, mabhathName, mabhathTitle,
           MIN(orderIndex) AS startIndex, MAX(orderIndex) AS endIndex
    FROM book_paragraphs
    WHERE partNumber = :partNumber
    GROUP BY mainSectionName, faslName, mabhathName
    ORDER BY MIN(orderIndex) ASC
"""
    )
    suspend fun getTableOfContents(partNumber: Int): List<TocItemTuple>


    @Query(
        """
    SELECT * FROM book_paragraphs
    WHERE partNumber = :partNumber AND orderIndex BETWEEN :startIndex AND :endIndex
    ORDER BY orderIndex
"""
    )
    suspend fun getRange(partNumber: Int, startIndex: Int, endIndex: Int): List<BookEntity>


    @Query(
        """
    SELECT * FROM book_paragraphs
    WHERE (:partNumber IS NULL OR partNumber = :partNumber)
      AND id IN (
        SELECT rowid FROM book_paragraphs_fts
        WHERE book_paragraphs_fts MATCH :ftsQuery
    )
    ORDER BY partNumber ASC, orderIndex ASC
    LIMIT :limit
"""
    )
    suspend fun search(
        ftsQuery: String,
        partNumber: Int? = null,
        limit: Int = 300
    ): List<BookEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighlight(highlight: BookHighlightEntity)

    @Query("SELECT * FROM favorite_highlights ORDER BY createdAt DESC")
    fun getAllHighlights(): Flow<List<BookHighlightEntity>>

    @Query("DELETE FROM favorite_highlights WHERE id = :id")
    suspend fun deleteHighlight(id: Int)
}