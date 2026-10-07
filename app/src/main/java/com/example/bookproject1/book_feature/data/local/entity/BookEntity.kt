package com.example.bookproject1.book_feature.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "book_paragraphs")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val partNumber: Int,             // رقم الجزء (1، 2، 3...)
    val orderIndex: Int,             // تسلسل الفقرة لضمان الترتيب عند العرض

    // العناوين الأبوية (تفيدنا بالبحث ولمعرفة هاي الفقرة تابعة لأي باب وفصل)
    val mainSectionName: String,     // "مقدمة الطبعة الأولى"، "سيرة الإمام"، أو "الباب الأول"
    val mainSectionTitle: String?,   // عنوان الباب إن وجد
    val faslName: String?,           // "الفصل الأول"
    val faslTitle: String?,          // عنوان الفصل
    val mabhathName: String?,        // "المبحث الأول"
    val mabhathTitle: String?,       // عنوان المبحث

    // المحتوى الفعلي
    val subheading: String?,         // العنوان الفرعي مثل "نسبه وحياته:" إن وجد
    val contentText: String          // نص الفقرة
)