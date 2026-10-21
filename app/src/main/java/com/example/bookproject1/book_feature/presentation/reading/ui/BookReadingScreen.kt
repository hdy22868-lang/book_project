package com.example.bookproject1.book_feature.presentation.reading.ui

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.bookproject1.R
import com.example.bookproject1.app.ui.theme.AlMasabeehTheme
import com.example.bookproject1.book_feature.domain.model.BookParagraph
import com.example.bookproject1.book_feature.presentation.reading.logic.BookReadingViewModel
import com.example.bookproject1.book_feature.presentation.reading.logic.TextHighlight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookReadingScreen(
    viewModel: BookReadingViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current
    val context = LocalContext.current

    DisposableEffect(view) {
        val window = (view.context as? Activity)?.window ?: return@DisposableEffect onDispose {}
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        onDispose {
            insetsController.show(WindowInsetsCompat.Type.navigationBars())
        }
    }

    var isDarkTheme by remember { mutableStateOf(true) }
    var fontSizeSp by remember { mutableFloatStateOf(18f) }
    var lineSpacingMultiplier by remember { mutableFloatStateOf(1.8f) }
    var letterSpacingSp by remember { mutableFloatStateOf(0.5f) }

    var showColorBar by remember { mutableStateOf(false) }
    var highlightsMap by remember { mutableStateOf(mapOf<Int, List<TextHighlight>>()) }

    var selectedParagraph by remember { mutableStateOf<BookParagraph?>(null) }
    var selectedText by remember { mutableStateOf<String?>(null) }

    AlMasabeehTheme(darkTheme = isDarkTheme) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = uiState.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            bottomBar = {
                ReadingBottomBar(
                    isDarkTheme = isDarkTheme,
                    fontSize = fontSizeSp,
                    lineSpacingMultiplier = lineSpacingMultiplier,
                    letterSpacingSp = letterSpacingSp,
                    showColorBar = showColorBar,
                    onToggleDarkTheme = { isDarkTheme = !isDarkTheme },
                    onIncreaseFontSize = { if (fontSizeSp < 28f) fontSizeSp += 2f },
                    onDecreaseFontSize = { if (fontSizeSp > 14f) fontSizeSp -= 2f },
                    onLineSpacingChange = { lineSpacingMultiplier = it },
                    onLetterSpacingChange = { letterSpacingSp = it },
                    onToggleColorBar = { showColorBar = !showColorBar },
                    onColorSelected = { chosenColor ->
                        val targetParagraph = selectedParagraph
                        val textToHighlight = selectedText

                        if (targetParagraph != null && !textToHighlight.isNullOrBlank()) {
                            val currentHls = highlightsMap[targetParagraph.id] ?: emptyList()
                            highlightsMap = highlightsMap + (
                                    targetParagraph.id to (currentHls + TextHighlight(textToHighlight, chosenColor))
                                    )
                            viewModel.saveHighlightWithColor(
                                textToHighlight,
                                targetParagraph,
                                chosenColor.toArgb().toLong()
                            )

                            Toast.makeText(context, "تمت الإضافة إلى المفضلة", Toast.LENGTH_SHORT).show()
                            showColorBar = false
                            selectedText = null
                            selectedParagraph = null
                        } else {
                            Toast.makeText(context, "حدد نصاً أولاً ثم اختر اللون", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when {
                    uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    uiState.isError -> {
                        Column(
                            Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(stringResource(R.string.cant_reload), color = MaterialTheme.colorScheme.onBackground)
                            Button(onClick = viewModel::loadContent) { Text(stringResource(R.string.retry)) }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val subtitle = uiState.subtitle
                            if (subtitle != null) {
                                item {
                                    Text(
                                        text = subtitle,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                            }

                            itemsIndexed(uiState.paragraphs, key = { _, p -> p.id }) { index, paragraph ->
                                val prevSub = if (index > 0) uiState.paragraphs[index - 1].subheading else null
                                val isNewSection = !paragraph.subheading.isNullOrBlank() && paragraph.subheading != prevSub

                                ParagraphItem(
                                    paragraph = paragraph,
                                    isNewSection = isNewSection,
                                    fontSize = fontSizeSp,
                                    lineSpacingMultiplier = lineSpacingMultiplier,
                                    letterSpacingSp = letterSpacingSp,
                                    highlights = highlightsMap[paragraph.id] ?: emptyList(),
                                    onTextSelected = { para, text ->
                                        selectedParagraph = para
                                        selectedText = text
                                        showColorBar = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}