package com.example.vanishclassnetwork

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.messageDao()

    // Simulated Network State
    private val _isNetworkAvailable = MutableStateFlow(true)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable

    private val _currentTime = MutableStateFlow(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime

    val messages = dao.getAllMessages().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val now = System.currentTimeMillis()
                _currentTime.value = now
                dao.deleteExpiredMessages(now)
            }
        }
    }

    // Toggle simulated network
    fun toggleNetwork() {
        _isNetworkAvailable.value = !_isNetworkAvailable.value
        if (_isNetworkAvailable.value) {
            sendPendingMessages() // Trigger queue flush when network returns
        }
    }

    fun sendMessage(text: String, sender: String) {
        if (text.isBlank()) return

        val now = System.currentTimeMillis()
        val status = if (_isNetworkAvailable.value) "SENT" else "PENDING"

        val newMessage = MessageEntity(
            id = UUID.randomUUID().toString(),
            sender = sender,
            text = text,
            sentAt = now,
            expiresAt = now + 30_000,
            status = status
        )

        viewModelScope.launch {
            dao.insertMessage(newMessage)

            // If network is ON, send it immediately using the mock Relay
            if (status == "SENT") {
                RelayService.sendMessageToRelay(newMessage)
            }
        }
    }

    // This handles sending offline messages exactly once when network returns
    private fun sendPendingMessages() {
        viewModelScope.launch {
            val pending = dao.getPendingMessages()
            val now = System.currentTimeMillis()

            for (message in pending) {
                if (message.expiresAt > now) {
                    // Message is still valid, try to send it to the mock relay
                    val success = RelayService.sendMessageToRelay(message)
                    if (success) {
                        dao.markAsSent(message.id)
                    }
                } else {
                    // If it expired while offline, delete it, do not send.
                    dao.deleteExpiredMessages(now)
                }
            }
        }
    }
}