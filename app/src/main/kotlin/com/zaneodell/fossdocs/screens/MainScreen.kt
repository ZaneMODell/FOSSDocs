package com.zaneodell.fossdocs.screens


import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaneodell.fossdocs.DocumentEvent
import com.zaneodell.fossdocs.DocumentState
import com.zaneodell.fossdocs.SortType
import com.zaneodell.fossdocs.models.data.SearchResults
import com.zaneodell.fossdocs.screencomponents.PdfPage
import com.zaneodell.fossdocs.utilities.DeviceUtils
import com.zaneodell.fossdocs.utilities.PdfBitmapConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Main screen of the app, shows a list of recent documents and a button to select a file.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(FlowPreview::class)
@Composable
fun MainScreen(
    fileUri: Uri?, // Added fileUri parameter for "Open with" functionality
    modifier: Modifier = Modifier,
    state: DocumentState,
    onEvent: (DocumentEvent) -> Unit,
) {
    val context = LocalContext.current
    val pdfBitmapConverter = remember { PdfBitmapConverter(context) }
    var localFileUri by remember { mutableStateOf(fileUri) }
    var renderedPages by remember { mutableStateOf(emptyList<Bitmap>()) }
    var searchResults by remember { mutableStateOf(emptyList<SearchResults>()) }
    var isLoading by remember { mutableStateOf(false) }
    var isWordDoc by remember { mutableStateOf(false) } // Track if the file is a Word document
    var wordContent by remember { mutableStateOf<String?>(null) } // Store Word document HTML content
    val scope = rememberCoroutineScope()

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // File picker launcher for PDF and Word files
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            localFileUri = it
        }
    }

    // Handle fileUri changes (e.g., from "Open with" or file picker)
    LaunchedEffect(key1 = localFileUri) {
        localFileUri?.let { uri ->
            when (context.contentResolver.getType(uri)) {
                "application/pdf" -> {
                    isLoading = true
                    isWordDoc = false
                    state.path = uri.toString()
                    state.lastOpened = System.currentTimeMillis()
                    state.name = uri.lastPathSegment.toString()
                    onEvent(DocumentEvent.SaveDocument)

                    renderedPages = pdfBitmapConverter.pdfToBitmaps(uri)
                    isLoading = false
                }

                "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> {
                    isLoading = true
                    isWordDoc = true
                    scope.launch {
//                        wordContent = convertWordToHtml(context, uri)
                        print("IS WORD DOC")
                        isLoading = false
                    }
                }
            }
        }
    }

    // If no file is selected, show the default screen
    if (state.documents.isNotEmpty() && (localFileUri == null)) {
        Scaffold(modifier = modifier) { innerPadding ->
            Column {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(100.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(650.dp),
                    contentPadding = innerPadding
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            verticalAlignment = CenterVertically
                        ) {
                            SortType.entries.forEach { sortType ->
                                Row(
                                    modifier = Modifier.clickable{
                                        onEvent(DocumentEvent.SortDocuments(sortType))
                                    },
                                    verticalAlignment = CenterVertically
                                ) {
                                    RadioButton(selected = state.sortType == sortType, onClick = {
                                        onEvent(DocumentEvent.SortDocuments(sortType))
                                    })
                                    Text(text = sortType.name)
                                }
                            }
                        }
                    }
                    items(state.documents) { document ->
//                        DocumentPreviewCard(documentPreviewVM)
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${document.name} ${document.lastOpened}",
                                    fontSize = 20.sp
                                )
                                Text(document.path, fontSize = 15.sp)
                            }
                            IconButton(onClick = {
                                onEvent(DocumentEvent.DeleteDocument(document))
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "delete the document")
                            }
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(onClick = {
                        filePickerLauncher.launch(
                            arrayOf(
                                "application/pdf",
                                "application/msword",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                            )
                        )
                    }) {
                        Text("Select a Document")
                    }
                }
            }
        }
    }
    else if (state.documents.isEmpty()){
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            FloatingActionButton(onClick = {
                filePickerLauncher.launch(
                    arrayOf(
                        "application/pdf",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    )
                )
            }) {
                Icon(Icons.Filled.AddCircle, "Floating Action Button")
            }
            Spacer(Modifier.padding(10.dp))
            Text("Select A Document")
        }

    }
    // Show the selected file (PDF or Word)
    else {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(DeviceUtils.getDeviceAspectRatio())
        ) {
            var sensitivity by remember { mutableFloatStateOf(2f) }

            val state = rememberTransformableState { zoomChange, panChange, _ ->
                val adjustedZoom = if (zoomChange < 1) {
                    1 - (1 - zoomChange) * sensitivity * 0.75f
                } else {
                    1 + (zoomChange - 1) * sensitivity
                }

                scale = (scale * adjustedZoom).coerceIn(1f, 5f)

                val extraWidth = (scale - 1) * constraints.maxWidth
                val extraHeight = (scale - 1) * constraints.maxHeight
                val maxX = extraWidth / 2
                val maxY = extraHeight / 2

                offset = Offset(
                    x = (offset.x + scale * panChange.x).coerceIn(-maxX, maxX),
                    y = (offset.y + scale * panChange.y).coerceIn(-maxY, maxY)
                )
            }

            Column(modifier = modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }
                .transformable(state),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    if (isWordDoc) {
                        // Render Word document
//                        wordContent?.let { html ->
//                            WordDocumentView(htmlContent = html)
//                        }
                        print("IS WORD DOC")
                    } else {
                        // Render PDF
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
                    }
                }

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(15.dp),
            ) {
                Column {
                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                            filePickerLauncher.launch(
                                arrayOf(
                                    "application/pdf",
                                    "application/msword",
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                                )
                            )
                        }) {
                        Text("Choose another Document")
                    }
                    //Version check for PDF searching
                    if (Build.VERSION.SDK_INT >= 35 && !isWordDoc) {
                        val searchQuery = remember { mutableStateOf("") }
                        LaunchedEffect(Unit) {
                            snapshotFlow { searchQuery.value }.debounce(300).distinctUntilChanged()
                                .collectLatest { query ->
                                    pdfBitmapConverter.renderer?.let { renderer ->
                                        searchResults = withContext(Dispatchers.IO) {
                                            (0 until renderer.pageCount).asSequence()
                                                .mapNotNull { index ->
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

                        OutlinedTextField(
                            value = searchQuery.value,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black),
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
    }
}

///**
// * Renders a Word document as HTML in a WebView.
// */
//@Composable
//fun WordDocumentView(htmlContent: String) {
//    AndroidView(
//        factory = { context ->
//            WebView(context).apply {
//                //TODO LOOK INTO THIS AS TO NOT INTRODUCE VULNERABILITIES
//                settings.javaScriptEnabled = true
//                webViewClient = WebViewClient()
//                loadDataWithBaseURL(
//                    null, htmlContent, "text/html", "UTF-8", null
//                )
//            }
//        })
//}
//
///**
// * Converts a Word document to HTML.
// */
//suspend fun convertWordToHtml(context: Context, uri: Uri): String {
//    return try {
//        val inputStream = context.contentResolver.openInputStream(uri)
//        val bytes = inputStream?.readBytes() ?: byteArrayOf()
//        val base64 = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
//        """
//        <html>
//            <head>
//                <script src="file:///android_asset/js/mammoth.browser.min.js"></script>
//                <style>
//                    body { font-family: sans-serif; line-height: 1.6; padding: 20px }
//                    table { border-collapse: collapse; width: 100% }
//                    td, th { border: 1px solid #ddd; padding: 8px }
//                    img { max-width: 100% }
//                </style>
//            </head>
//            <body>
//                <div id="content"></div>
//                <script>
//                    const base64 = "$base64";
//                    const raw = atob(base64);
//                    const array = new Uint8Array(raw.length);
//                    for (let i = 0; i < raw.length; i++) array[i] = raw.charCodeAt(i);
//
//                    mammoth.convertToHtml({ arrayBuffer: array.buffer })
//                        .then(result => {
//                            document.getElementById("content").innerHTML = result.value;
//                        })
//                        .catch(err => {
//                            document.body.innerHTML = "Error rendering document: " + err.message;
//                        });
//                </script>
//            </body>
//        </html>
//        """
//    } catch (e: Exception) {
//        "Error rendering document: ${e.localizedMessage}"
//    }
//}