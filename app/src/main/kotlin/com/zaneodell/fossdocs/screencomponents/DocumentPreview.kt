package com.zaneodell.fossdocs.screencomponents

import Document
import android.content.ContentResolver
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.zaneodell.fossdocs.DocumentEvent

@Composable
fun DocumentPreview(
    document: Document,
    onEvent: (DocumentEvent) -> Unit,
    contentResolver: ContentResolver
){
    Column(
        modifier = Modifier.padding(6.dp)
    ) {
        val docUri = document.path.toUri()

        val thumbnailBitmap = try {
            contentResolver.loadThumbnail(docUri, Size(30, 30), null)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        Image(
            bitmap = thumbnailBitmap?.asImageBitmap() ?: ImageBitmap(1,1),
            contentDescription = document.name,
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = document.name,
            modifier = Modifier
                .padding(top = 4.dp)
                .align(Alignment.CenterHorizontally),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 12.sp
        )
    }
}