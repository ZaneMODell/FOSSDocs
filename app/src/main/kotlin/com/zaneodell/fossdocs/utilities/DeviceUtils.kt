package com.zaneodell.fossdocs.utilities

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration


/**
 * Utility singleton class for device-related operations.
 */
object DeviceUtils {
    /**
     * Function that calculates the device's aspect ratio.
     */
    @Composable
    fun getDeviceAspectRatio(): Float {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        val screenHeight = configuration.screenHeightDp
        return screenWidth.toFloat() / screenHeight.toFloat()
    }

    fun getFileNameFromUri(uri: Uri, contentResolver: ContentResolver): String? {
        // Check if the URI is a content URI
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (it.moveToFirst()) {
                    return it.getString(nameIndex)
                }
            }
        } else if (uri.scheme == "file") {
            // If it's a file URI, get the last path segment
            return uri.lastPathSegment
        }
        return null
    }
}