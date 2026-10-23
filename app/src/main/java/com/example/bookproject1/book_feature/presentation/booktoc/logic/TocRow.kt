package com.example.bookproject1.book_feature.presentation.booktoc.logic

import com.example.bookproject1.book_feature.domain.model.TocNode

data class TocRow(
    val node: TocNode,
    val depth: Int
)

fun flatten(
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