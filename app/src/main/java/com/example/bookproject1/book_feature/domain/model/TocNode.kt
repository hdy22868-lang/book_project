package com.example.bookproject1.book_feature.domain.model

import java.util.UUID

sealed interface TocNode {
    val id: String
    val title: String

    data class Leaf(
        override val id: String = UUID.randomUUID().toString(),
        override val title: String,
        val startIndex: Int,
        val endIndex: Int
    ) : TocNode

    data class Branch(
        override val id: String = UUID.randomUUID().toString(),
        override val title: String,
        val children: List<TocNode>
    ) : TocNode
}