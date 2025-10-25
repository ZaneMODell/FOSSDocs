package com.zaneodell.fossdocs.screencomponents

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
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
import com.zaneodell.fossdocs.utilities.DocumentThumbnailUtils

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

        bitmap = DocumentThumbnailUtils.generateThumbnailFromUri(context, fileUri)
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


