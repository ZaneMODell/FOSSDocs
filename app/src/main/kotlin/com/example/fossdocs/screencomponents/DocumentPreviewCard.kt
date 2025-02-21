package com.example.fossdocs.screencomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.fossdocs.R
import com.example.fossdocs.models.view.DocumentPreviewVM


/**
 * Displays a preview of a document in the recent documents screen.
 */
@Composable
fun DocumentPreviewCard(doc: DocumentPreviewVM) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        doc.previewImageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = doc.title,
                modifier = Modifier.size(100.dp)
            )
        } ?: doc.previewImageRes?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = doc.title,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Text(
            text = doc.title,
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Preview of the DocumentPreviewCard composable.
 */
@Preview(showBackground = true)
@Composable
fun DocumentPreviewPreview() {
    DocumentPreviewCard(
        doc = DocumentPreviewVM(
            title = "Sample Document",
            previewImageRes = R.drawable.cat // Replace with your drawable
        )
    )
}