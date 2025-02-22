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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.withContext

/**
 * Main screen of the app, shows a list of recent documents and a button to select a file.
 */
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val pdfBitmapConverter = remember { PdfBitmapConverter(context) }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var renderedPages by remember { mutableStateOf(emptyList<Bitmap>()) }
    var searchResults by remember { mutableStateOf(emptyList<SearchResults>()) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        fileUri = uri
    }

    //This happens when the fileUri changes. This will change the rendered pages and recomposition will occur.
    //TODO Look more into LaunchedEffect to understand what it does.
    LaunchedEffect(key1 = fileUri) {
        fileUri?.let { uri ->
            renderedPages = pdfBitmapConverter.pdfToBitmaps(uri)
        }

    }

    //If we do not have a PDF or other file currently selected, we show a list of recent documents.
    if (fileUri == null) {
        Scaffold(modifier = modifier) { innerPadding ->
            Column {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(100.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(650.dp),
                    contentPadding = innerPadding
                ) {
                    items(sampleDocs) { documentPreviewVM ->
                        DocumentPreviewCard(documentPreviewVM)
                    }

                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(onClick = { filePickerLauncher.launch("application/pdf") }) {
                        Text("Select a PDF")
                    }
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
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(renderedPages) { index, page ->
                    PdfPage(
                        page,
                        modifier = modifier,
                        searchResults = searchResults.find { it.page == index })
                }
            }
            Button(onClick = { filePickerLauncher.launch("application/pdf") }) {
                Text("Choose another PDF")
            }
            //TODO FIGURE OUT HOW THIS IF BLOCK WORKS
            if (Build.VERSION.SDK_INT >= 35) {
                val searchQuery = remember { mutableStateOf("") }

                // Use snapshotFlow to debounce search input changes
                LaunchedEffect(Unit) {
                    snapshotFlow { searchQuery.value }.debounce(300) // Adjust debounce delay
                        .distinctUntilChanged() // Prevent unnecessary recomputation
                        .filter { it.isNotBlank() } // Ignore empty searches
                        .collectLatest { query ->
                            pdfBitmapConverter.renderer?.let { renderer ->
                                searchResults = withContext(Dispatchers.IO) {
                                    (0 until renderer.pageCount).asSequence().mapNotNull { index ->
                                        renderer.openPage(index).use { page ->
                                            val results = page.searchText(query)
                                            if (results.isNotEmpty()) {
                                                SearchResults(
                                                    index,
                                                    results.map { it.bounds.first() })
                                            } else null
                                        }
                                    }.toList()
                                }
                            }
                        }
                }

                OutlinedTextField(value = searchQuery.value,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (searchQuery.value.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery.value = ""
                                searchResults = emptyList()
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    onValueChange = { newText -> searchQuery.value = newText },
                    label = { Text("Search") })
            }


        }
    }
}

/**
 * Renders a single page of a PDF.
 */
@Composable
fun PdfPage(
    page: Bitmap,
    modifier: Modifier = Modifier,
    searchResults: SearchResults? = null,
) {
    //TODO LOOK AT THIS CODE MORE TO FULLY UNDERSTAND IT
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
