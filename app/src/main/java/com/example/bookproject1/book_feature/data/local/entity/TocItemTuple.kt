package com.example.bookproject1.book_feature.data.local.entity

// كلاس مساعد نستخدمه بس حتى نجلب أسماء العناوين من القاعدة
data class TocItemTuple(
    val mainSectionName: String,
    val mainSectionTitle: String?,
    val faslName: String?,
    val faslTitle: String?,
    val mabhathName: String?,
    val mabhathTitle: String?,
    val startIndex: Int,
    val endIndex: Int
)