package com.example.vanishclassnetwork

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun JoinScreen(
    onJoin: (String, String) -> Unit // Now takes Name and URL
) {
    var displayName by remember { mutableStateOf("") }
    // Pre-filled with the protocol URL
    var relayUrl by remember { mutableStateOf("wss://vanish-relay-sawaira.bonto.page/ws") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Vanish Class Network",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Enter a display name to join the class chat.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = displayName,
            onValueChange = {
                displayName = it
                errorMessage = null
            },
            label = { Text("Display Name") },
            singleLine = true,
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = relayUrl,
            onValueChange = { relayUrl = it },
            label = { Text("Relay URL") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val trimmedName = displayName.trim()
                if (trimmedName.isEmpty()) {
                    errorMessage = "Display name cannot be empty"
                } else if (trimmedName.length < 3) {
                    errorMessage = "Name must be at least 3 characters"
                } else if (relayUrl.isBlank()) {
                    errorMessage = "Relay URL cannot be empty"
                } else {
                    onJoin(trimmedName, relayUrl)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Join Chat", fontSize = 18.sp)
        }
    }
}