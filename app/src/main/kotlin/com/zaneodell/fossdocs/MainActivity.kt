package com.zaneodell.fossdocs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.zaneodell.fossdocs.screens.MainScreen
import com.zaneodell.fossdocs.ui.theme.FOSSDocsTheme

/**
 * Entry point of the app.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState current state of the application
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle the incoming intent (e.g., "Open with" from another app)
        val fileUri = handleIntent(intent)

        setContent {
            FOSSDocsTheme {
                // Pass the fileUri to MainScreen
                var localFileUri by remember { mutableStateOf(fileUri) }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        fileUri = localFileUri, modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    /**
     * Function that handles new intents, specifically for opening documents in this app
     * outside of the app.
     * @param intent The new intent that triggered this function.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle new intents (e.g., if the user opens another file while the app is already running)
        intent.let {
            val fileUri = handleIntent(it)
            setContent {
                FOSSDocsTheme {
                    var localFileUri by remember { mutableStateOf(fileUri) }
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        MainScreen(
                            fileUri = localFileUri, modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    /**
     * Function that processes the intent and returns the URI if it's a supported file type.
     *
     * @param intent The intent to process.
     */
    private fun handleIntent(intent: Intent): Uri? {
        return when (intent.action) {
            Intent.ACTION_VIEW -> {
                val uri = intent.data
                if (uri != null && isSupportedFileType(uri)) {
                    uri // Return the URI if it's a supported file type
                } else {
                    null
                }
            }

            else -> null
        }
    }

    /**
     * Takes in a URI and returns true if it's a supported file type.
     *
     * @param uri The URI to check.
     */
    private fun isSupportedFileType(uri: Uri): Boolean {
        val mimeType = contentResolver.getType(uri)
        return when (mimeType) {
            "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> true
            else -> false
        }
    }
}