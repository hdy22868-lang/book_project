package com.example.bookproject1.book_feature.presentation.reading.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookproject1.book_feature.domain.model.BookParagraph
import com.example.bookproject1.book_feature.presentation.reading.logic.TextHighlight

@Composable
fun ParagraphItem(
    paragraph: BookParagraph,
    isNewSection: Boolean,
    fontSize: Float,
    lineSpacingMultiplier: Float,
    letterSpacingSp: Float,
    highlights: List<TextHighlight>,
    onTextSelected: (BookParagraph, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val annotatedContent = remember(paragraph.contentText, highlights) {
        buildAnnotatedString {
            append(paragraph.contentText)
            highlights.forEach { hl ->
                val cleanHl = hl.text.trim()
                if (cleanHl.isNotEmpty()) {
                    var startIndex = paragraph.contentText.indexOf(cleanHl)
                    while (startIndex != -1) {
                        val endIndex = startIndex + cleanHl.length
                        addStyle(
                            style = SpanStyle(
                                background = hl.color.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Bold
                            ),
                            start = startIndex,
                            end = endIndex
                        )
                        startIndex = paragraph.contentText.indexOf(cleanHl, endIndex)
                    }
                }
            }
        }
    }

    var selection by remember { mutableStateOf(TextRange.Zero) }

    LaunchedEffect(highlights) {
        selection = TextRange.Zero
    }

    val textFieldValue = remember(annotatedContent, selection) {
        TextFieldValue(
            annotatedString = annotatedContent,
            selection = selection
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (isNewSection) {
            Text(
                text = paragraph.subheading.orEmpty(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = (fontSize + 2).sp,
                    lineHeight = ((fontSize + 2) * 1.5).sp
                ),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = if (paragraph.subheading.orEmpty().contains("الرَّحْمَـٰنِ")) {
                    TextAlign.Center
                } else {
                    TextAlign.Start
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, bottom = 8.dp)
            )
        }

        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                selection = newValue.selection
                if (!newValue.selection.collapsed) {
                    val start = newValue.selection.min.coerceIn(0, paragraph.contentText.length)
                    val end = newValue.selection.max.coerceIn(0, paragraph.contentText.length)
                    if (end > start) {
                        val selectedSub = paragraph.contentText.substring(start, end).trim()
                        if (selectedSub.isNotEmpty()) {
                            onTextSelected(paragraph, selectedSub)
                        }
                    }
                }
            },
            readOnly = true,
            cursorBrush = SolidColor(Color.Transparent),
            textStyle = TextStyle(
                fontSize = fontSize.sp,
                lineHeight = (fontSize * lineSpacingMultiplier).sp,
                letterSpacing = letterSpacingSp.sp,
                textAlign = TextAlign.Justify,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}