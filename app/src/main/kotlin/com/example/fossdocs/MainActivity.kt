package com.example.fossdocs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fossdocs.screens.MainScreen
import com.example.fossdocs.ui.theme.FOSSDocsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FOSSDocsTheme {
                MainScreen()
            }
        }
    }
}
