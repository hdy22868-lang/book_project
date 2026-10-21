package com.example.bookproject1.book_feature.presentation.reading.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookproject1.R

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
            ReadingColorPickerPopup(
                onColorSelected = onColorSelected,
                onDismiss = onToggleColorBar
            )
        }

        ReadingSpacingDialog(
            visible = showSpacingDialog,
            lineSpacingMultiplier = lineSpacingMultiplier,
            letterSpacingSp = letterSpacingSp,
            onLineSpacingChange = onLineSpacingChange,
            onLetterSpacingChange = onLetterSpacingChange,
            onDismiss = { showSpacingDialog = false }
        )

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
                        contentDescription = stringResource(R.string.toggle_theme),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = {
                    showSpacingDialog = !showSpacingDialog
                }) {
                    Icon(
                        imageVector = Icons.Default.FormatLineSpacing,
                        contentDescription = stringResource(R.string.text_spacing),
                        tint = if (showSpacingDialog) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = {
                    showSpacingDialog = false
                    onToggleColorBar()
                }) {
                    Icon(
                        imageVector = Icons.Default.BorderColor,
                        contentDescription = stringResource(R.string.highlight_color),
                        tint = if (showColorBar) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onIncreaseFontSize) {
                    Text(
                        text = "A+",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onDecreaseFontSize) {
                    Text(
                        text = "A-",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}