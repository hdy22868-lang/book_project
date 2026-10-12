package com.example.bookproject1.book_feature.presentation.home.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.bookproject1.R
import com.example.bookproject1.app.ui.theme.AlMasabeehTheme
import com.example.bookproject1.book_feature.presentation.component.logic.BookPart
import com.example.bookproject1.book_feature.presentation.home.logic.HomeAction
import com.example.bookproject1.book_feature.presentation.home.logic.HomeUiState
import com.example.bookproject1.book_feature.presentation.home.logic.HomeViewModel

// 1. الشاشة الرئيسية (التي تتعامل مع الـ ViewModel)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onPartClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        onToggleLayout = { viewModel.onAction(HomeAction.OnToggleLayoutClick) },
        onPartClick = onPartClick,
        modifier = modifier
    )
}

// 2. شاشة المحتوى (واجهة مجردة تأخذ الـ State فقط وتسهل الـ Preview)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onToggleLayout: () -> Unit,
    onPartClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.home_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        IconButton(onClick = onToggleLayout) {
                            Icon(
                                imageVector = if (uiState.isGridLayout) {
                                    Icons.AutoMirrored.Filled.ViewList
                                } else {
                                    Icons.Default.GridView
                                },
                                contentDescription = stringResource(id = R.string.toggle_layout),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            if (uiState.isGridLayout) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(uiState.parts, key = { it.partNumber }) { part ->
                        PartGridCard(
                            part = part,
                            onClick = onPartClick
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(uiState.parts, key = { it.partNumber }) { part ->
                        PartListCard(
                            part = part,
                            onClick = onPartClick
                        )
                    }
                }
            }
        }
    }
}

// ===================================
// Previews (نستخدم HomeScreenContent لنتجنب خطأ الـ ViewModel)
// ===================================

private val mockParts = listOf(
    BookPart(1, R.string.part_one, R.drawable.part1),
    BookPart(2, R.string.part_two, R.drawable.part2),
    BookPart(3, R.string.part_three, R.drawable.part3),
    BookPart(4, R.string.part_four, R.drawable.part4),
    BookPart(5, R.string.part_five, R.drawable.part5)
)

@Preview(name = "HomeScreen - Light", showBackground = true)
@Composable
private fun HomeScreenLightPreview() {
    AlMasabeehTheme(darkTheme = false) {
        Surface {
            // متغير مؤقت للـ Preview فقط
            var isGrid by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }

            HomeScreenContent(
                uiState = HomeUiState(parts = mockParts, isGridLayout = isGrid),
                onToggleLayout = { isGrid = !isGrid }, // تغيير الحالة عند الضغط
                onPartClick = {}
            )
        }
    }
}

@Preview(name = "HomeScreen - Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenDarkPreview() {
    AlMasabeehTheme(darkTheme = true) {
        Surface {
            var isGrid by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

            HomeScreenContent(
                uiState = HomeUiState(parts = mockParts, isGridLayout = isGrid),
                onToggleLayout = { isGrid = !isGrid },
                onPartClick = {}
            )
        }
    }
}