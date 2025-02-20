package com.example.fossdocs.viewmodels
import android.net.Uri
import androidx.annotation.DrawableRes
import com.example.fossdocs.R

/**
 * View model for a document preview.
 */
data class DocumentPreviewVM(
    val title: String = "",
    val previewImageUri: Uri? = null,  // For file-based images
    @DrawableRes val previewImageRes: Int? = null // For drawable resources
)

val sampleDocs = listOf(
    DocumentPreviewVM("Sample Cat", previewImageRes = R.drawable.cat),
    DocumentPreviewVM("Sample Dog", previewImageRes = R.drawable.dog),
    DocumentPreviewVM("Sample Cat 2", previewImageRes = R.drawable.cat),
    DocumentPreviewVM("Sample Dog 2", previewImageRes = R.drawable.dog),
//    DocumentPreviewVM("Local Image", previewImageUri = Uri.parse("content:/Internal storage/DCIM/Camera/20250209_193754.jpg"))
)

