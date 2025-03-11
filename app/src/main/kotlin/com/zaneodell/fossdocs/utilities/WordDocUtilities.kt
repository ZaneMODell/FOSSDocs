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

}