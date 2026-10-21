package com.example.bookproject1.book_feature.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "book_paragraphs",
        indices = [
        Index(value = ["partNumber", "orderIndex"], unique = true),
        Index(value = ["partNumber", "mainSectionName", "faslName", "mabhathName"])
        ]
)

data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val partNumber: Int,
    val orderIndex: Int,


    val mainSectionName: String,
    val mainSectionTitle: String?,
    val faslName: String?,
    val faslTitle: String?,
    val mabhathName: String?,
    val mabhathTitle: String?,


    val subheading: String?,
    val contentText: String,
    val searchText: String
)