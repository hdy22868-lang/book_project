package com.example.bookproject1.book_feature.presentation.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookproject1.app.ui.theme.UiFontFamily
import com.example.bookproject1.book_feature.presentation.component.ui.BottomBar
import com.example.bookproject1.book_feature.presentation.component.ui.BottomTab
import com.example.bookproject1.book_feature.presentation.component.ui.BottomBarGold
import com.example.bookproject1.book_feature.presentation.search.logic.SearchAction
import com.example.bookproject1.book_feature.presentation.search.logic.SearchUiState
import com.example.bookproject1.book_feature.presentation.search.logic.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit = onNavigateBack,
    onNavigateSettings: () -> Unit = {},
    onResultClick: (partNumber: Int, orderIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    SearchContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
        onNavigateHome = onNavigateHome,
        onNavigateSettings= onNavigateSettings,
        onResultClick = onResultClick,
        modifier = modifier
    )
}

@Composable
fun SearchContent(
    uiState: SearchUiState,
    onAction: (SearchAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit = onNavigateBack,
    onNavigateSettings: () -> Unit = {},
    onResultClick: (partNumber: Int, orderIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .statusBarsPadding()
                        .padding(top = 10.dp, bottom = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            tonalElevation = 2.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = if (uiState.query.isNotBlank()) BottomBarGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    modifier = Modifier.size(22.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (uiState.query.isEmpty()) {
                                        Text(
                                            text = "بحث ...",
                                            fontFamily = UiFontFamily,
                                            fontSize = 14.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                                        )
                                    }

                                    BasicTextField(
                                        value = uiState.query,
                                        onValueChange = { onAction(SearchAction.OnQueryChange(it)) },
                                        singleLine = true,
                                        textStyle = TextStyle(
                                            fontFamily = UiFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        cursorBrush = SolidColor(BottomBarGold),
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                        keyboardActions = KeyboardActions(onSearch = {
                                            focusManager.clearFocus()
                                            onAction(SearchAction.OnSearchSubmit)
                                        }),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                if (uiState.query.isNotEmpty()) {
                                    IconButton(
                                        onClick = { onAction(SearchAction.OnClearQuery) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "مسح",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = uiState.selectedPartFilter == null,
                            onClick = { onAction(SearchAction.OnFilterPartSelect(null)) },
                            shape = RoundedCornerShape(10.dp),
                            label = {
                                Text(
                                    text = "كل الأجزاء",
                                    fontFamily = UiFontFamily,
                                    fontWeight = if (uiState.selectedPartFilter == null) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.5.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BottomBarGold.copy(alpha = 0.22f),
                                selectedLabelColor = BottomBarGold,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = null
                        )

                        for (part in 1..5) {
                            val isPartSelected = uiState.selectedPartFilter == part
                            FilterChip(
                                selected = isPartSelected,
                                onClick = { onAction(SearchAction.OnFilterPartSelect(part)) },
                                shape = RoundedCornerShape(10.dp),
                                label = {
                                    Text(
                                        text = "الجزء $part",
                                        fontFamily = UiFontFamily,
                                        fontWeight = if (isPartSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.5.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BottomBarGold.copy(alpha = 0.22f),
                                    selectedLabelColor = BottomBarGold,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = null
                            )
                        }
                    }
                }
            },
            // 👈 البوتوم بار المضاف
            bottomBar = {
                BottomBar(
                    currentTab = BottomTab.SEARCH,
                    onTabSelected = { tab ->
                        when (tab) {
                            BottomTab.HOME -> onNavigateHome()
                            BottomTab.SETTINGS -> onNavigateSettings() // 👈 الانتقال للإعدادات
                            BottomTab.SEARCH -> {}
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
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            color = BottomBarGold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    uiState.errorMessage != null -> {
                        Text(
                            text = uiState.errorMessage,
                            fontFamily = UiFontFamily,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    !uiState.isSearched && uiState.query.isBlank() -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "اكتب كلمة أو عبارة للبحث ",
                                fontFamily = UiFontFamily,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    uiState.isSearched && uiState.filteredResults.isEmpty() -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "لم يتم العثور على نتائج تطابق «${uiState.query}»",
                                fontFamily = UiFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Text(
                                    text = "تم العثور على ${uiState.filteredResults.size} نتيجة",
                                    fontFamily = UiFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                                )
                            }

                            items(uiState.filteredResults, key = { it.id }) { result ->
                                SearchResultCard(
                                    paragraph = result,
                                    searchQuery = uiState.query,
                                    onClick = {
                                        onResultClick(result.partNumber, result.orderIndex)
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