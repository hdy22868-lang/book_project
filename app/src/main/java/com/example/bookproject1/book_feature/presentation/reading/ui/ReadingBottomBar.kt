package com.example.bookproject1.book_feature.presentation.reading.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.bookproject1.R

val HighlightColors = listOf(
    Color(0xFFFFD54F),
    Color(0xFFEF8519),
    Color(0xFF92765D),
    Color(0xFF93B4D7)
)

@Composable
fun ReadingBottomBar(
    isDarkTheme: Boolean,
    fontSize: Float,
    lineSpacingMultiplier: Float,
    letterSpacingSp: Float,
    showColorBar: Boolean,
    onToggleDarkTheme: () -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onLineSpacingChange: (Float) -> Unit,
    onLetterSpacingChange: (Float) -> Unit,
    onToggleColorBar: () -> Unit,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSpacingDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {

        if (showColorBar) {
            Popup(
                alignment = Alignment.BottomCenter,
                offset = IntOffset(0, -150),
                properties = PopupProperties(focusable = false)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 12.dp,
                    shadowElevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.choose_color),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            HighlightColors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .clickable { onColorSelected(color) }
                                )
                            }
                        }

                        IconButton(onClick = onToggleColorBar) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق")
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showSpacingDialog,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.string_distance),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showSpacingDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق")
                        }
                    }

                    Text(
                        text = stringResource(
                            R.string.line_distance,
                            (lineSpacingMultiplier * 10).toInt() / 10f
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = lineSpacingMultiplier,
                        onValueChange = onLineSpacingChange,
                        valueRange = 1.3f..2.5f
                    )

                    Text(
                        text = stringResource(
                            R.string.line_distence,
                            (letterSpacingSp * 10).toInt() / 10f
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = letterSpacingSp,
                        onValueChange = onLetterSpacingChange,
                        valueRange = 0f..3f
                    )
                }
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onToggleDarkTheme) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "تبديل الثيم",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = {
                    showSpacingDialog = !showSpacingDialog
                }) {
                    Icon(
                        imageVector = Icons.Default.FormatLineSpacing,
                        contentDescription = "تباعد النصوص",
                        tint = if (showSpacingDialog) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = {
                    showSpacingDialog = false
                    onToggleColorBar()
                }) {
                    Icon(
                        imageVector = Icons.Default.BorderColor,
                        contentDescription = "تظليل النص",
                        tint = if (showColorBar) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecreaseFontSize) {
                        Text("A-", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(
                        text = "${fontSize.toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(onClick = onIncreaseFontSize) {
                        Text("A+", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}