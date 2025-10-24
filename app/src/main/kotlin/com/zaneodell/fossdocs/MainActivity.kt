package com.zaneodell.fossdocs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zaneodell.fossdocs.database.DatabaseProvider
import com.zaneodell.fossdocs.models.view.DocumentViewModel
import com.zaneodell.fossdocs.screens.MainScreen
import com.zaneodell.fossdocs.ui.theme.FOSSDocsTheme

/**
 * Entry point of the app.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created.
     *
     */
    private val database by lazy { DatabaseProvider.getDatabase(this) }

    private val viewModel by viewModels<DocumentViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DocumentViewModel(database.documentDao()) as T
                }
            }
        }
    )


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
                    val state by viewModel.state.collectAsState()
                    MainScreen(
                        fileUri = localFileUri, modifier = Modifier.padding(innerPadding), state = state, onEvent=viewModel::onEvent
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
                        val state by viewModel.state.collectAsState()
                        MainScreen(
                            fileUri = localFileUri, modifier = Modifier.padding(innerPadding), state = state, onEvent=viewModel::onEvent
                        )
                    }
                }
            }
        }
    }

    /**
     * Processes an incoming intent and returns a URI if it's a supported document type.
     * Grants and persists read access to the URI if needed.
     *
     * @param intent The intent to process.
     * @return The valid document URI, or null if unsupported or inaccessible.
     */
    private fun handleIntent(intent: Intent): Uri? {
        val uri = intent.data
        if ((intent.action == Intent.ACTION_VIEW || intent.action == Intent.ACTION_OPEN_DOCUMENT) &&
            uri != null && isSupportedFileType(uri)) {
            try {
                // Persist URI permission if available
                val takeFlags = intent.flags and
                        (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                // Check for read permission
                if (takeFlags and Intent.FLAG_GRANT_READ_URI_PERMISSION != 0) {
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                // Check for write permission
                if (takeFlags and Intent.FLAG_GRANT_WRITE_URI_PERMISSION != 0) {
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }

            } catch (e: SecurityException) {
                e.printStackTrace()
                return null
            }
            return uri
        }
        return null
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