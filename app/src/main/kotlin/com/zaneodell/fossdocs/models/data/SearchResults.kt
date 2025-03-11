package com.zaneodell.fossdocs.models.data

import android.graphics.RectF

/**
 * Data class to represent search results for a specific document.
 */
data class SearchResults(
    val page: Int, val results: List<RectF>
)
