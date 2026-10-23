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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookproject1.app.ui.theme.UiFontFamily
import com.example.bookproject1.book_feature.domain.model.BookParagraph
import com.example.bookproject1.book_feature.presentation.component.ui.BottomBarGold
import com.example.bookproject1.book_feature.presentation.search.logic.SearchHighlightUtils.buildSmartSnippet

@Composable
fun SearchResultCard (
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

    val breadcrumbs = remember(paragraph) {
        listOfNotNull(
            paragraph.mainSectionName.takeIf { it.isNotBlank() },
            paragraph.faslName?.takeIf { it.isNotBlank() },
            paragraph.mabhathName?.takeIf { it.isNotBlank() }
        ).joinToString(" › ")
    }


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


            if (!paragraph.subheading.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = paragraph.subheading,
                    fontFamily = UiFontFamily,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


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
