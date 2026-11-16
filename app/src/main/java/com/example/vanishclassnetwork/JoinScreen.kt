package com.example.vanishclassnetwork

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun validateDisplayName(name: String): Result<String> {
    val trimmed = name.trim()
    return when {
        trimmed.isEmpty() -> Result.failure(IllegalArgumentException("Display name cannot be empty"))
        trimmed.length < 3 -> Result.failure(IllegalArgumentException("Name must be at least 3 characters"))
        else -> Result.success(trimmed)
    }
}

@Composable
fun JoinScreen(
    onJoin: (String) -> Unit
) {
    var displayName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val handleJoin = {
        validateDisplayName(displayName).fold(
            onSuccess = { validName ->
                onJoin(validName)
            },
            onFailure = { error ->
                errorMessage = error.message
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { handleJoin() }),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { handleJoin() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Join Chat", fontSize = 18.sp)
        }
    }
}
