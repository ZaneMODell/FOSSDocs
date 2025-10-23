package com.zaneodell.fossdocs.models.data

import android.graphics.RectF

/**
 * Data class to represent search results for a specific document.
 *
 * @param page The page number of the document.
 * @param results The list of search results. These are yellow highlighted rectangles.
 */
data class SearchResults(
    val page: Int, val results: List<RectF>
)
