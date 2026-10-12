package com.example.bookproject1.book_feature.domain.model

import java.util.UUID

sealed interface TocNode {
    // استخدمنا String لتوليد معرف فريد لكل عنصر (مهم جداً لعمل القوائم المنسدلة في Compose بسلاسة)
    val id: String
    val title: String

    // يمثل العنصر النهائي القابل للقراءة (مثل: مبحث، أو مقدمة بدون تفرعات)
    data class Leaf(
        override val id: String = UUID.randomUUID().toString(),
        override val title: String,
        val startIndex: Int,
        val endIndex: Int
    ) : TocNode

    // يمثل القائمة المنسدلة (مثل: باب أو فصل يحتوي على أجزاء أخرى داخله)
    data class Branch(
        override val id: String = UUID.randomUUID().toString(),
        override val title: String,
        val children: List<TocNode> // هنا نخزن العناصر المتداخلة (الفصول والمباحث)
    ) : TocNode
}