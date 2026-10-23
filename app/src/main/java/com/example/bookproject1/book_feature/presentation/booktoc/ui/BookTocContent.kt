package com.example.bookproject1.book_feature.presentation.booktoc.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.bookproject1.R
import com.example.bookproject1.book_feature.presentation.booktoc.logic.BookTocUiState
import com.example.bookproject1.book_feature.presentation.booktoc.logic.flatten
import com.example.bookproject1.book_feature.presentation.component.logic.BookPart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookTocContent(
    uiState: BookTocUiState,
    onNavigateBack: () -> Unit,
    onToggle: (String) -> Unit,
    onRetry: () -> Unit,
    onNodeClick: (Int, Int) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(BookPart.titleResOf(uiState.partNumber)),
                        style = MaterialTheme.typography.titleLarge
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
                        verticalArrangement = Arrangement.spacedBy(10.dp),
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