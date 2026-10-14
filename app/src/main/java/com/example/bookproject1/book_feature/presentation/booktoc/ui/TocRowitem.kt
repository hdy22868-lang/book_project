package com.example.bookproject1.book_feature.presentation.booktoc.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bookproject1.book_feature.domain.model.TocNode

data class TocRow(val node: TocNode, val depth: Int)

@Composable
fun TocRowItem(
    row: TocRow,
    isExpanded: Boolean,
    onToggle: (String) -> Unit,
    onLeafClick: (Int, Int) -> Unit
) {
    val node = row.node
    val isBranch = node is TocNode.Branch

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if (row.depth % 2 == 0) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.surface
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (row.depth * 14).dp) // في RTL يعني إزاحة من اليمين
            .clickable {
                when (node) {
                    is TocNode.Branch -> onToggle(node.id)
                    is TocNode.Leaf -> onLeafClick(node.startIndex, node.endIndex)
                }
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = node.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isBranch) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            if (isBranch) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}