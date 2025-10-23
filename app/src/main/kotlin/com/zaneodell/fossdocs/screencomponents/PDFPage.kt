package com.zaneodell.fossdocs.screencomponents

import android.graphics.Bitmap
import android.graphics.RectF
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.zaneodell.fossdocs.models.data.SearchResults

/**
 * Renders a single page of a PDF.
 *
 * @param page The bitmap of the page.
 * @param modifier The modifier for the page.
 * @param searchResults The search results for the page. This will highlight all of the results
 * in yellow.
 */
@Composable
fun PdfPage(
    page: Bitmap,
    modifier: Modifier = Modifier,
    searchResults: SearchResults? = null,
) {
    //Allows the fetching of an image asynchronously as to not block main thread
    AsyncImage(
        model = page,
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(page.width.toFloat() / page.height.toFloat())
            .drawWithContent {
                drawContent()

                val scaleFactorX = size.width / page.width
                val scaleFactorY = size.height / page.height
                searchResults?.results?.forEach { rect ->
                    val adjustedRect = RectF(
                        rect.left * scaleFactorX,
                        rect.top * scaleFactorY,
                        rect.right * scaleFactorX,
                        rect.bottom * scaleFactorY

                    )
                    /**
                     * This is for all of the search results within a PDF file.
                     * Will be highlighted in yellow.
                     */
                    drawRoundRect(
                        color = Color.Yellow.copy(alpha = 0.5f), topLeft = Offset(
                            x = adjustedRect.left, y = adjustedRect.top
                        ), size = Size(
                            width = adjustedRect.width(), height = adjustedRect.height()
                        ), cornerRadius = CornerRadius(5.dp.toPx())
                    )
                }
            })
}