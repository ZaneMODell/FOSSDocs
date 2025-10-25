package com.zaneodell.fossdocs.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utility singleton class for document thumbnail-related operations.
 */
object DocumentThumbnailUtils {

    val PDF_MIMETYPE = "application/pdf"


    /**
     * Takes in a URI and returns a thumbnail of the file.
     *
     * @param context The context of the application.
     * @param uri The URI of the file.
     * @return The generated thumbnail, generally the first page. Or null if an error occurs.
     */
    suspend fun generateThumbnailFromUri(context: Context, uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                if (isPdf(context, uri)) {
                    generatePdfThumbnail(context, uri)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Helper function that returns true if the file is a PDF.
     *
     * @param context The context of the application.
     * @param uri The URI of the file.
     * @return True if the file is a PDF, false otherwise.
     */
    private fun isPdf(context: Context, uri: Uri): Boolean {
        return context.contentResolver.getType(uri) == PDF_MIMETYPE
    }


    /**
     * Generates a thumbnail for a PDF file.
     *
     * @param context The context of the application.
     * @param uri The URI of the PDF file.
     * @return The generated thumbnail, or null if an error occurred.
     */
//TODO go through this function and document it better. I currently have no idea what's going on.
    private fun generatePdfThumbnail(context: Context, uri: Uri): Bitmap? {
        return try {
            //The ParcelFileDescriptor is a low-level handle to an open file. The "r" says we are in read-only mode
            val pfd: ParcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r") ?: return null
            //This just creates an instance of Android's PDFRenderer class, which takes a reference to a PDF file descriptor
            val renderer = PdfRenderer(pfd)
            //This opens the first page of the PDF, is a PdfRenderer.Page object
            val page = renderer.openPage(0)
            //Create an empty bitmap to hold the page of the page's dimensions to not crop or distort the bitmap
            val bitmap = createBitmap(page.width, page.height)
            //This function renders the page actually onto our created bitmap
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            //These next 3 lines just clean up existing resources
            page.close()
            renderer.close()
            pfd.close()
            //Our created bitmap is returned
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}