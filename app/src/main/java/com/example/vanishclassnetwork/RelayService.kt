package com.example.vanishclassnetwork

import kotlinx.coroutines.delay

// This is a MOCK interface. When you get the real server URL,
// you will replace the contents of sendMessageToRelay with actual WebSocket/OkHttp code.
object RelayService {

    // Simulates sending a message to the server
    suspend fun sendMessageToRelay(message: MessageEntity): Boolean {
        // Simulate network delay (e.g., 500ms)
        delay(500)

        // In the future, this is where you will send the JSON to the class relay.
        // For now, we just return true to say "it was sent successfully".
        return true
    }
}