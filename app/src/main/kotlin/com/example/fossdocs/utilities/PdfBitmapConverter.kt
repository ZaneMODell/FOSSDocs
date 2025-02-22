package com.example.fossdocs.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * Converts a PDF file to a list of bitmaps to then be rendered in the document viewer screen
 */
class PdfBitmapConverter(
    private val context: Context
) {

    var renderer: PdfRenderer? = null

    /**
     * Converts a PDF file to a list of bitmaps asynchronously.
     *
     * @param contentUri The URI of the PDF file.
     */
    suspend fun pdfToBitmaps(contentUri: Uri): List<Bitmap> {
        return withContext(Dispatchers.IO) {
            renderer?.close()

            context.contentResolver.openFileDescriptor(contentUri, "r")?.use { descriptor ->
                with(PdfRenderer(descriptor)) {
                    renderer = this

                    return@withContext (0 until pageCount).map { index ->
                        async {
                            openPage(index).use { page ->
                                val bitmap = Bitmap.createBitmap(
                                    page.width, page.height, Bitmap.Config.ARGB_8888
                                )
                                Canvas(bitmap).apply {
                                    drawColor(Color.WHITE)
                                    drawBitmap(bitmap, 0f, 0f, null)
                                }
                                page.render(
                                    bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                                )
                                bitmap
                            }
                        }
                    }.awaitAll()
                }
            }
            return@withContext emptyList()
        }
    }
}