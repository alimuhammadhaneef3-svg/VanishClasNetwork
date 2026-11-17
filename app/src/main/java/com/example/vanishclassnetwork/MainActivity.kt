package com.example.vanishclassnetwork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VanishApp()
                }
            }
        }

    }
}

@Composable
fun VanishApp() {
    var isJoined by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf("") }
    var relayUrl by remember { mutableStateOf("") } // <-- Added to hold the URL

    if (!isJoined) {
        JoinScreen(
            onJoin = { name, url ->
                currentUser = name
                relayUrl = url
                isJoined = true
            }
        )
    } else {
        ChatScreen(
            currentUser = currentUser,
            relayUrl = relayUrl // <-- Passed to ChatScreen
        )
    }
}