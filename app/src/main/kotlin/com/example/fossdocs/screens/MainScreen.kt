package com.example.fossdocs.screens

import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.fossdocs.models.data.SearchResults
import com.example.fossdocs.models.view.sampleDocs
import com.example.fossdocs.screencomponents.DocumentPreviewCard
import com.example.fossdocs.utilities.PdfBitmapConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main screen of the app, shows a list of recent documents and a button to select a file.
 */
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val pdfBitmapConverter = remember { PdfBitmapConverter(context) }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var renderedPages by remember { mutableStateOf(emptyList<Bitmap>()) }
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(emptyList<SearchResults>()) }
    val selectedFileName by remember { mutableStateOf<String?>(null) }
    val selectedMimeType by remember { mutableStateOf<String?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        fileUri = uri
    }

    val scope = rememberCoroutineScope()

    //This happens when the fileUri changes. This will change the rendered pages and recomposition will occur.
    // Look more into LaunchedEffect to understand what it does.
    LaunchedEffect(key1 = fileUri) {
        fileUri?.let { uri->
            renderedPages = pdfBitmapConverter.pdfToBitmaps(uri)
        }

    }

    //If we do not have a PDF or other file currently selected, we show a list of recent documents.
    if(fileUri == null){
        Scaffold(modifier = modifier, snackbarHost = {
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
            Column {
                LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(650.dp),
                    contentPadding = innerPadding) {
                    items(sampleDocs) { documentPreviewVM ->
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
    //We show the PDF/other file type
    else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(modifier = Modifier
                .weight(1f)
                .fillMaxWidth())
            {
                itemsIndexed(renderedPages) {index, page ->
                    PdfPage(page, modifier = modifier, searchResults = searchResults.find { it.page == index })
                }
            }
            Button(onClick = {filePickerLauncher.launch("application/pdf")}) {
                Text("Choose another PDF")
            }
            if(Build.VERSION.SDK_INT >= 35) {
                OutlinedTextField(
                    value = searchText,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = {
                                searchText = ""
                                searchResults = emptyList()
                            }) {
                                Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    onValueChange ={
                        newSearchText ->
                        searchText = newSearchText
                        pdfBitmapConverter.renderer?.let { renderer ->
                            scope.launch(Dispatchers.Default) {
                                searchResults = (0 until renderer.pageCount).map { index ->
                                    renderer.openPage(index).use { page ->
                                        val results = page.searchText(searchText)

                                        val matchedRects = results.map { result ->
                                            result.bounds.first()
                                        }

                                        SearchResults(index, matchedRects)
                                    }
                                }
                            }
                        }
                    },
                    label = {
                        Text("Search")
                    }
                )
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
    searchResults: SearchResults? = null,
) {
    AsyncImage(
        model = page,
        contentDescription = null,
        modifier = modifier.fillMaxWidth()
            .aspectRatio(page.width.toFloat() / page.height.toFloat())
            .drawWithContent{
                drawContent()

                val scaleFactorX = size.width / page.width
                val scaleFactorY = size.height / page.height
                searchResults?.results?.forEach{ rect->
                    val adjustedRect = RectF(
                        rect.left * scaleFactorX,
                        rect.top * scaleFactorY,
                        rect.right * scaleFactorX,
                        rect.bottom * scaleFactorY

                    )

                    drawRoundRect(
                        color = Color.Yellow.copy(alpha = 0.5f),
                        topLeft = Offset(
                            x = adjustedRect.left,
                            y = adjustedRect.top
                        ),
                        size = Size(
                            width = adjustedRect.width(),
                            height = adjustedRect.height()
                        ),
                        cornerRadius = CornerRadius(5.dp.toPx())
                    )
                }
            }
    )
}
