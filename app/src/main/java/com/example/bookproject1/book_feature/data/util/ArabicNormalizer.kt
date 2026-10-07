package com.example.bookproject1.book_feature.data.util

object ArabicNormalizer {
    private val diacritics = Regex("[\\u064B-\\u065F\\u0670\\u0640]")

    fun normalize(s: String): String = s
        .replace(diacritics, "")
        .replace('أ', 'ا').replace('إ', 'ا').replace('آ', 'ا').replace('ٱ', 'ا')
        .replace('ى', 'ي').replace('ة', 'ه')
}