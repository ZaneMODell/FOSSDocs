package com.zaneodell.fossdocs.utilities

import android.content.Context
import android.net.Uri
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument

//TODO FIGURE OUT HOW THIS WORKS AND IMPLEMENT IT
object WordDocUtilities {
    fun extractTextFromWordFile(context: Context, uri: Uri): String {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            try {
                if (uri.toString().endsWith(".docx")) {
                    val document = XWPFDocument(inputStream)
                    document.paragraphs.joinToString("\n") { it.text }
                } else {
                    val document = XWPFDocument(inputStream)
                    val extractor = XWPFWordExtractor(document)
                    extractor.text
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Error reading document"
            }
        } ?: "Failed to open file"
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

}