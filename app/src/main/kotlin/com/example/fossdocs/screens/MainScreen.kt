package com.example.fossdocs.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.fossdocs.screencomponents.DocumentPreviewCard
import com.example.fossdocs.utilities.PdfBitmapConverter
import com.example.fossdocs.viewmodels.sampleDocs

/**
 * Main screen of the app, shows a list of recent documents and a button to select a file.
 */
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val pdfBitmapConverter = remember { PdfBitmapConverter(context) }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var renderedPages by remember { mutableStateOf(emptyList<Bitmap>()) }
    val selectedFileName by remember { mutableStateOf<String?>(null) }
    val selectedMimeType by remember { mutableStateOf<String?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        fileUri = uri
    }

    //This happens when the fileUri changes. This will change the rendered pages and recomposition will occur.
    // Look more into LaunchedEffect to understand what it does.
    LaunchedEffect(key1 = fileUri) {
        fileUri?.let { uri->
            renderedPages = pdfBitmapConverter.pdfToBitmaps(uri)
        }

    }

    //If we do not have a PDF or other file currently selected, we show a list of recent documents.
    if(fileUri == null){
        Scaffold(modifier = Modifier.fillMaxSize(), snackbarHost = {
            if (showSnackbar) {
                Snackbar(action = {
                    TextButton(onClick = { showSnackbar = false }) {
                        Text("Dismiss")
                    }
                }) {
                    Text("File: ${selectedFileName.orEmpty()}, Type: ${selectedMimeType.orEmpty()}")
                }
            }
        }) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row {
                    sampleDocs.forEach { documentPreviewVM ->
                        DocumentPreviewCard(documentPreviewVM)
                    }
                }
                Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { filePickerLauncher.launch("application/pdf") }) {
                    Text("Select a PDF")
                }
            }
        }
    }
    //We show the PDF
    else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(modifier = Modifier
                .weight(1f)
                .fillMaxWidth())
            {
                items(renderedPages) {page ->
                    PdfPage(page, modifier = modifier)
                }
            }
        }
    }
}


///**
// * Helper function to get the file name from a URI.
// *
// * @param contentResolver The ContentResolver to use for querying the URI.
// */
//fun getFileName(contentResolver: ContentResolver, uri: Uri): String {
//    val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
//    return cursor?.use {
//        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//        it.moveToFirst()
//        it.getString(nameIndex)
//    } ?: "Unknown"
//}

@Composable
fun PdfPage(
    page: Bitmap,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = page,
        contentDescription = null,
        modifier = modifier.fillMaxWidth()
            .aspectRatio(page.width.toFloat() / page.height.toFloat())
    )
}
