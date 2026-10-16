package com.example.bookproject1.book_feature.presentation.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookproject1.app.ui.theme.UiFontFamily
import com.example.bookproject1.book_feature.domain.model.BookParagraph
import com.example.bookproject1.book_feature.presentation.component.ui.BottomBarGold

@Composable
fun SearchResultCard(
    paragraph: BookParagraph,
    searchQuery: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val partName = when (paragraph.partNumber) {
        1 -> "الجزء الأول"
        2 -> "الجزء الثاني"
        3 -> "الجزء الثالث"
        4 -> "الجزء الرابع"
        5 -> "الجزء الخامس"
        else -> "الجزء ${paragraph.partNumber}"
    }

    // المسار الهرمي
    val breadcrumbs = remember(paragraph) {
        listOfNotNull(
            paragraph.mainSectionName.takeIf { it.isNotBlank() },
            paragraph.faslName?.takeIf { it.isNotBlank() },
            paragraph.mabhathName?.takeIf { it.isNotBlank() }
        ).joinToString(" › ")
    }

    // اقتطاع ذكي يركز على مكان الكلمة المبحوث عنها ويظللها بالأصفر
    val highlightedSnippet = remember(paragraph.contentText, searchQuery) {
        buildSmartSnippet(
            fullText = paragraph.contentText,
            rawQuery = searchQuery
        )
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // شارة الجزء والمسار
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BottomBarGold.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = partName,
                        fontFamily = UiFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = BottomBarGold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (breadcrumbs.isNotBlank()) {
                    Text(
                        text = breadcrumbs,
                        fontFamily = UiFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // العنوان الفرعي
            if (!paragraph.subheading.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = paragraph.subheading,
                    fontFamily = UiFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // مقتطف النص الذكي الذي يظهر الكلمات المبحوث عنها مباشرة ومظللة بالأصفر
            Text(
                text = highlightedSnippet,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.5.sp,
                lineHeight = 22.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// أداة تنظيف الحروف العربية ومطابقة الهمزات والحركات
private val diacriticsRegex = Regex("[\\u064B-\\u065F\\u0670\\u0640]")

private fun normalizeArabic(text: String): String {
    return text.replace(diacriticsRegex, "")
        .replace('أ', 'ا').replace('إ', 'ا').replace('آ', 'ا').replace('ٱ', 'ا')
        .replace('ى', 'ي').replace('ة', 'ه')
}

/**
 * دالة ذكية لاقتطاع المقطع المحيط مباشرة بالكلمات المبحوث عنها وتظليلها باللون الأصفر
 */
fun buildSmartSnippet(fullText: String, rawQuery: String): AnnotatedString {
    val cleanQuery = rawQuery.trim()
    if (cleanQuery.isBlank()) return AnnotatedString(fullText.take(180))

    // 1. استخراج كلمات البحث وتوسيع مرادفاتها الإملائية (مثل: أبو / أبي / أبا)
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

    // 2. مطابقة النص الأصلي مع النص المجرّد لمعرفة إحداثيات الحروف بدقة
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

    // 3. البحث عن أفضل مكان تتواجد فيه كلمات البحث
    var bestMatchNormIndex = -1
    for (kw in expandedKeywords) {
        val idx = normText.indexOf(kw)
        if (idx != -1) {
            if (bestMatchNormIndex == -1 || idx > bestMatchNormIndex) {
                bestMatchNormIndex = idx
            }
        }
    }

    // فحص خاص إذا وُجدت كلمتان معاً (مثل: دعاء + حمزة) لضبط النافذة حولهما بدقة
    val pDua = normText.indexOf("دعاء")
    val pHamza = normText.indexOf("حمزه")
    if (pDua != -1 && pHamza != -1) {
        bestMatchNormIndex = minOf(pDua, pHamza)
    }

    val bestOrigIndex = if (bestMatchNormIndex != -1 && bestMatchNormIndex < normToOrig.size) {
        normToOrig[bestMatchNormIndex]
    } else 0

    // 4. اقتطاع نافذة محيطة بالكلمات المبحوث عنها (~180 حرف) بمحاذاة حدود الكلمات
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

    // 5. تظليل الكلمات داخل المقتطف باللون الأصفر الفاتح والخلفية المظللة
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

    return buildAnnotatedString {
        append(snippet)
        for (kw in expandedKeywords) {
            var startIndex = 0
            while (startIndex < snippetNorm.length) {
                val matchIdx = snippetNorm.indexOf(kw, startIndex)
                if (matchIdx == -1) break

                val matchEndNorm = matchIdx + kw.length
                if (matchIdx < snippetNormToOrig.size && (matchEndNorm - 1) < snippetNormToOrig.size) {
                    val origStart = snippetNormToOrig[matchIdx]
                    val origEnd = snippetNormToOrig[matchEndNorm - 1] + 1
                    addStyle(
                        style = SpanStyle(
                            color = BottomBarGold,
                            background = BottomBarGold.copy(alpha = 0.25f),
                            fontWeight = FontWeight.Bold
                        ),
                        start = origStart,
                        end = origEnd
                    )
                }
                startIndex = matchIdx + kw.length
            }
        }
    }
}

/**
 * دالة إضافية لتظليل الكلمات المبحوث عنها داخل شاشة القراءة نفسها (ReadingScreen)
 */
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

    return buildAnnotatedString {
        append(fullText)
        for (kw in expandedKeywords) {
            var startIndex = 0
            while (startIndex < normText.length) {
                val matchIdx = normText.indexOf(kw, startIndex)
                if (matchIdx == -1) break

                val matchEndNorm = matchIdx + kw.length
                if (matchIdx < normToOrig.size && (matchEndNorm - 1) < normToOrig.size) {
                    val origStart = normToOrig[matchIdx]
                    val origEnd = normToOrig[matchEndNorm - 1] + 1
                    addStyle(
                        style = SpanStyle(
                            color = BottomBarGold,
                            background = BottomBarGold.copy(alpha = 0.25f),
                            fontWeight = FontWeight.Bold
                        ),
                        start = origStart,
                        end = origEnd
                    )
                }
                startIndex = matchIdx + kw.length
            }
        }
    }
}