package com.example.bookproject1.book_feature.presentation.search.logic

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.example.bookproject1.book_feature.presentation.component.ui.BottomBarGold

class SearchHighlightUtils {
    private val ZWJ = "\u200D"
    private val NON_CONNECTING_TO_NEXT = setOf('أ', 'إ', 'آ', 'ا', 'ٱ', 'د', 'ذ', 'ر', 'ز', 'و', 'ؤ', 'ء', 'ة')
    private val DIACRITICS = setOf('ً', 'ٌ', 'ٍ', 'َ', 'ُ', 'ِ', 'ّ', 'ْ', 'ٓ', 'ٔ')

    private fun isDiacritic(c: Char): Boolean =
        c in DIACRITICS || (c in '\u064B'..'\u065F') || c == '\u0670' || c == '\u0640'

    private fun isArabicLetter(c: Char): Boolean =
        c in '\u0600'..'\u06FF' && !isDiacritic(c)

    private fun connectsToNext(ch: Char?): Boolean =
        ch != null && ch !in NON_CONNECTING_TO_NEXT && isArabicLetter(ch)

    private fun connectsToPrev(ch: Char?): Boolean =
        ch != null && ch != 'ء' && isArabicLetter(ch)

    private fun shouldConnectLeft(text: String, start: Int): Boolean {
        if (start <= 0) return false
        var idx = start - 1
        while (idx >= 0 && isDiacritic(text[idx])) {
            idx--
        }
        if (idx < 0 || !isArabicLetter(text[idx])) return false
        val prevChar = text[idx]

        var idxM = start
        while (idxM < text.length && isDiacritic(text[idxM])) {
            idxM++
        }
        if (idxM >= text.length || !isArabicLetter(text[idxM])) return false
        val firstMatchChar = text[idxM]

        return connectsToNext(prevChar) && connectsToPrev(firstMatchChar)
    }
    private fun shouldConnectRight(text: String, end: Int): Boolean {
        if (end >= text.length) return false
        var idx = end
        while (idx < text.length && isDiacritic(text[idx])) {
            idx++
        }
        if (idx >= text.length || !isArabicLetter(text[idx])) return false
        val nextChar = text[idx]

        var idxM = end - 1
        while (idxM >= 0 && isDiacritic(text[idxM])) {
            idxM--
        }
        if (idxM < 0 || !isArabicLetter(text[idxM])) return false
        val lastMatchChar = text[idxM]

        return connectsToNext(lastMatchChar) && connectsToPrev(nextChar)
    }

    private val diacriticsRegex = Regex("[\\u064B-\\u065F\\u0670\\u0640]")

    private fun normalizeArabic(text: String): String {
        return text.replace(diacriticsRegex, "")
            .replace('أ', 'ا').replace('إ', 'ا').replace('آ', 'ا').replace('ٱ', 'ا')
            .replace('ى', 'ي').replace('ة', 'ه')
    }

    fun buildSmartSnippet(fullText: String, rawQuery: String): AnnotatedString {
        val cleanQuery = rawQuery.trim()
        if (cleanQuery.isBlank()) return AnnotatedString(fullText.take(180))

        val queryWords = cleanQuery.split(Regex("[^\\p{L}\\p{N}]+"))
            .filter { it.length >= 2 }
            .map { normalizeArabic(it) }

        if (queryWords.isEmpty()) return AnnotatedString(fullText.take(180))

        val expandedKeywords = mutableSetOf<String>()
        for (w in queryWords) {
            expandedKeywords.add(w)
            if (w == "ابو" || w == "ابي" || w == "ابا") {
                expandedKeywords.addAll(listOf("ابو", "ابي", "ابا"))
            }
        }

        val normToOrig = ArrayList<Int>(fullText.length)
        val normTextBuilder = StringBuilder(fullText.length)

        for (i in fullText.indices) {
            val ch = fullText[i]
            val norm = normalizeArabic(ch.toString())
            if (norm.isNotEmpty()) {
                normTextBuilder.append(norm)
                normToOrig.add(i)
            }
        }
        val normText = normTextBuilder.toString()

        var bestMatchNormIndex = -1
        for (kw in expandedKeywords) {
            val idx = normText.indexOf(kw)
            if (idx != -1) {
                if (bestMatchNormIndex == -1 || idx > bestMatchNormIndex) {
                    bestMatchNormIndex = idx
                }
            }
        }

        val pDua = normText.indexOf("دعاء")
        val pHamza = normText.indexOf("حمزه")
        if (pDua != -1 && pHamza != -1) {
            bestMatchNormIndex = minOf(pDua, pHamza)
        }

        val bestOrigIndex = if (bestMatchNormIndex != -1 && bestMatchNormIndex < normToOrig.size) {
            normToOrig[bestMatchNormIndex]
        } else 0

        var windowStart = maxOf(0, bestOrigIndex - 40)
        if (windowStart > 0) {
            val nextSpace = fullText.indexOf(' ', windowStart)
            if (nextSpace in windowStart..(windowStart + 15)) {
                windowStart = nextSpace + 1
            }
        }

        var windowEnd = minOf(fullText.length, bestOrigIndex + 140)
        if (windowEnd < fullText.length) {
            val prevSpace = fullText.lastIndexOf(' ', windowEnd)
            if (prevSpace in (windowEnd - 15)..windowEnd) {
                windowEnd = prevSpace
            }
        }

        val prefix = if (windowStart > 0) "… " else ""
        val suffix = if (windowEnd < fullText.length) " …" else ""
        val snippet = prefix + fullText.substring(windowStart, windowEnd) + suffix

        val snippetNormToOrig = ArrayList<Int>(snippet.length)
        val snippetNormBuilder = StringBuilder(snippet.length)
        for (i in snippet.indices) {
            val ch = snippet[i]
            val norm = normalizeArabic(ch.toString())
            if (norm.isNotEmpty()) {
                snippetNormBuilder.append(norm)
                snippetNormToOrig.add(i)
            }
        }
        val snippetNorm = snippetNormBuilder.toString()

        val rawMatches = mutableListOf<Pair<Int, Int>>()
        for (kw in expandedKeywords) {
            var startIndex = 0
            while (startIndex < snippetNorm.length) {
                val matchIdx = snippetNorm.indexOf(kw, startIndex)
                if (matchIdx == -1) break

                val matchEndNorm = matchIdx + kw.length
                if (matchIdx < snippetNormToOrig.size && (matchEndNorm - 1) < snippetNormToOrig.size) {
                    val origStart = snippetNormToOrig[matchIdx]
                    var origEnd = snippetNormToOrig[matchEndNorm - 1] + 1
                    while (origEnd < snippet.length && isDiacritic(snippet[origEnd])) {
                        origEnd++
                    }
                    rawMatches.add(origStart to origEnd)
                }
                startIndex = matchIdx + kw.length
            }
        }

        val sortedMatches = rawMatches.sortedBy { it.first }
        val mergedMatches = mutableListOf<Pair<Int, Int>>()
        for (m in sortedMatches) {
            if (mergedMatches.isEmpty()) {
                mergedMatches.add(m)
            } else {
                val last = mergedMatches.last()
                if (m.first <= last.second) {
                    mergedMatches[mergedMatches.size - 1] = last.first to maxOf(last.second, m.second)
                } else {
                    mergedMatches.add(m)
                }
            }
        }

        return buildAnnotatedString {
            var lastIdx = 0
            for ((start, end) in mergedMatches) {
                val connectLeft = shouldConnectLeft(snippet, start)
                val connectRight = shouldConnectRight(snippet, end)

                if (start > lastIdx) {
                    var preText = snippet.substring(lastIdx, start)
                    if (lastIdx > 0 && shouldConnectRight(snippet, lastIdx)) {
                        preText = ZWJ + preText
                    }
                    if (connectLeft) {
                        preText += ZWJ
                    }
                    append(preText)
                }

                var mText = snippet.substring(start, end)
                if (connectLeft) {
                    mText = ZWJ + mText
                }
                if (connectRight) {
                    mText = mText + ZWJ
                }

                val highlightStart = length
                append(mText)
                val highlightEnd = length

                addStyle(
                    style = SpanStyle(
                        color = BottomBarGold,
                        background = BottomBarGold.copy(alpha = 0.28f)
                    ),
                    start = highlightStart,
                    end = highlightEnd
                )

                lastIdx = end
            }

            if (lastIdx < snippet.length) {
                var postText = snippet.substring(lastIdx)
                if (lastIdx > 0 && shouldConnectRight(snippet, lastIdx)) {
                    postText = ZWJ + postText
                }
                append(postText)
            }
        }
    }

    fun highlightFullText(fullText: String, rawQuery: String?): AnnotatedString {
        if (rawQuery.isNullOrBlank()) return AnnotatedString(fullText)

        val queryWords = rawQuery.trim().split(Regex("[^\\p{L}\\p{N}]+"))
            .filter { it.length >= 2 }
            .map { normalizeArabic(it) }

        if (queryWords.isEmpty()) return AnnotatedString(fullText)

        val expandedKeywords = mutableSetOf<String>()
        for (w in queryWords) {
            expandedKeywords.add(w)
            if (w == "ابو" || w == "ابي" || w == "ابا") {
                expandedKeywords.addAll(listOf("ابو", "ابي", "ابا"))
            }
        }

        val normToOrig = ArrayList<Int>(fullText.length)
        val normTextBuilder = StringBuilder(fullText.length)
        for (i in fullText.indices) {
            val ch = fullText[i]
            val norm = normalizeArabic(ch.toString())
            if (norm.isNotEmpty()) {
                normTextBuilder.append(norm)
                normToOrig.add(i)
            }
        }
        val normText = normTextBuilder.toString()

        val rawMatches = mutableListOf<Pair<Int, Int>>()
        for (kw in expandedKeywords) {
            var startIndex = 0
            while (startIndex < normText.length) {
                val matchIdx = normText.indexOf(kw, startIndex)
                if (matchIdx == -1) break

                val matchEndNorm = matchIdx + kw.length
                if (matchIdx < normToOrig.size && (matchEndNorm - 1) < normToOrig.size) {
                    val origStart = normToOrig[matchIdx]
                    var origEnd = normToOrig[matchEndNorm - 1] + 1
                    while (origEnd < fullText.length && isDiacritic(fullText[origEnd])) {
                        origEnd++
                    }
                    rawMatches.add(origStart to origEnd)
                }
                startIndex = matchIdx + kw.length
            }
        }

        val sortedMatches = rawMatches.sortedBy { it.first }
        val mergedMatches = mutableListOf<Pair<Int, Int>>()
        for (m in sortedMatches) {
            if (mergedMatches.isEmpty()) {
                mergedMatches.add(m)
            } else {
                val last = mergedMatches.last()
                if (m.first <= last.second) {
                    mergedMatches[mergedMatches.size - 1] = last.first to maxOf(last.second, m.second)
                } else {
                    mergedMatches.add(m)
                }
            }
        }

        return buildAnnotatedString {
            var lastIdx = 0
            for ((start, end) in mergedMatches) {
                val connectLeft = shouldConnectLeft(fullText, start)
                val connectRight = shouldConnectRight(fullText, end)

                if (start > lastIdx) {
                    var preText = fullText.substring(lastIdx, start)
                    if (lastIdx > 0 && shouldConnectRight(fullText, lastIdx)) {
                        preText = ZWJ + preText
                    }
                    if (connectLeft) {
                        preText += ZWJ
                    }
                    append(preText)
                }

                var mText = fullText.substring(start, end)
                if (connectLeft) {
                    mText = ZWJ + mText
                }
                if (connectRight) {
                    mText = mText + ZWJ
                }

                val highlightStart = length
                append(mText)
                val highlightEnd = length

                addStyle(
                    style = SpanStyle(
                        color = BottomBarGold,
                        background = BottomBarGold.copy(alpha = 0.28f)
                    ),
                    start = highlightStart,
                    end = highlightEnd
                )

                lastIdx = end
            }

            if (lastIdx < fullText.length) {
                var postText = fullText.substring(lastIdx)
                if (lastIdx > 0 && shouldConnectRight(fullText, lastIdx)) {
                    postText = ZWJ + postText
                }
                append(postText)
            }
        }
    }
}