package com.example.fossdocs.screencomponents

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
import com.example.fossdocs.models.data.SearchResults

/**
 * Renders a single page of a PDF.
 */
@Composable
fun PdfPage(
    page: Bitmap,
    modifier: Modifier = Modifier,
    searchResults: SearchResults? = null,
) {
    AsyncImage(model = page,
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