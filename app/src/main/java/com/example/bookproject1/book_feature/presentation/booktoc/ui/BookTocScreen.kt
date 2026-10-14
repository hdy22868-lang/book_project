package com.example.bookproject1.book_feature.presentation.booktoc.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.bookproject1.R
import com.example.bookproject1.book_feature.domain.model.TocNode
import com.example.bookproject1.book_feature.presentation.booktoc.logic.BookTocUiState
import com.example.bookproject1.book_feature.presentation.booktoc.logic.BookTocViewModel


// نحوّل الشجرة إلى قائمة مسطحة تحتوي فقط على العناصر الظاهرة حسب المفتوح/المغلق
private fun flatten(
    nodes: List<TocNode>,
    expandedIds: Set<String>,
    depth: Int = 0
): List<TocRow> {
    val result = mutableListOf<TocRow>()
    for (node in nodes) {
        result.add(TocRow(node, depth))
        if (node is TocNode.Branch && node.id in expandedIds) {
            result.addAll(flatten(node.children, expandedIds, depth + 1))
        }
    }
    return result
}

@Composable
fun BookTocScreen(
    viewModel: BookTocViewModel,
    onNavigateBack: () -> Unit,
    onNodeClick: (startIndex: Int, endIndex: Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    BookTocContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onToggle = viewModel::onToggle,
        onRetry = viewModel::load,
        onNodeClick = onNodeClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookTocContent(
    uiState: BookTocUiState,
    onNavigateBack: () -> Unit,
    onToggle: (String) -> Unit,
    onRetry: () -> Unit,
    onNodeClick: (Int, Int) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.part_title, uiState.partNumber),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_button_desc)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
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

                    uiState.isError -> Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(stringResource(R.string.toc_load_error))
                        Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
                    }

                    uiState.nodes.isEmpty() -> Text(
                        text = stringResource(R.string.no_chapters_found),
                        modifier = Modifier.align(Alignment.Center)
                    )

                    else -> {
                        val rows = remember(uiState.nodes, uiState.expandedIds) {
                            flatten(uiState.nodes, uiState.expandedIds)
                        }
                        LazyColumn(
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(rows, key = { it.node.id }) { row ->
                                TocRowItem(
                                    row = row,
                                    isExpanded = row.node.id in uiState.expandedIds,
                                    onToggle = onToggle,
                                    onLeafClick = onNodeClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
