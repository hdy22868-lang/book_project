package com.example.bookproject1.book_feature.data.local.entity

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

@Fts4(contentEntity = BookEntity::class, tokenizer = FtsOptions.TOKENIZER_UNICODE61)
@Entity(tableName = "book_paragraphs_fts")
data class BookFtsEntity(
    val searchText: String
)