package com.zaneodell.fossdocs.screencomponents

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Composable for a document thumbnail. Bitmap inside of the function generated via a helper method.
 *
 * @param context The context of the application.
 * @param fileUri The URI of the file.
 * @param modifier The modifier for the thumbnail
 */
@Composable
fun DocumentThumbnail(
    context: Context,
    fileUri: Uri?,
    modifier: Modifier = Modifier
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(fileUri) {
        if (fileUri == null) return@LaunchedEffect

        bitmap = loadThumbnailOrGenerate(context, fileUri)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(120.dp)
    ) {
        if (bitmap != null) {
            Image(bitmap = bitmap!!.asImageBitmap(), contentDescription = "Document Thumbnail")
        } else {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "No Preview Available",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

//TODO This function doesn't really load a thumbnail, it kinda just generates one?
private suspend fun loadThumbnailOrGenerate(context: Context, uri: Uri): Bitmap? {
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
    val mimeType = context.contentResolver.getType(uri)
    return mimeType == "application/pdf"
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
        val pfd: ParcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r") ?: return null
        val renderer = PdfRenderer(pfd)
        val page = renderer.openPage(0)
        val bitmap = createBitmap(page.width, page.height)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        renderer.close()
        pfd.close()
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
