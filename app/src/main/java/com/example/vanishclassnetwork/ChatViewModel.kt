package com.example.vanishclassnetwork

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.messageDao()

    private var webSocket: WebSocket? = null
    private var timeOffset: Long = 0L // Protocol 9: server_time - phone_time

    private val _isNetworkAvailable = MutableStateFlow(false)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable

    private val _currentTime = MutableStateFlow(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime

    val messages = dao.getAllMessages().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        startClock()
    }

    private fun startClock() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val now = System.currentTimeMillis()
                _currentTime.value = now
                // Local cleanup (also done by server 'expired' event)
                dao.deleteExpiredMessages(now)
            }
        }
    }

    // Protocol 1 & 9: Connect, Join, and Sync Clock
    fun connectToRelay(url: String, name: String) {
        if (webSocket != null) return // Prevent multiple connections

        val client = OkHttpClient.Builder()
            .pingInterval(15, TimeUnit.SECONDS) // Protocol 6: Heartbeat
            .build()

        val request = Request.Builder().url(url).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _isNetworkAvailable.value = true
                val join = JSONObject().apply {
                    put("type", "join")
                    put("protocol", 1)
                    put("device_id", UUID.randomUUID().toString())
                    put("name", name)
                }
                webSocket.send(join.toString())
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val json = JSONObject(text)
                    when (json.getString("type")) {
                        "welcome" -> {
                            // Protocol 9: Calculate Clock Offset
                            val serverTime = json.getLong("server_time")
                            timeOffset = serverTime - System.currentTimeMillis()
                        }
                        "message" -> {
                            // Protocol 3: Receive Message
                            val msg = MessageEntity(
                                id = json.getString("id"),
                                sender = json.getString("sender"),
                                text = json.getString("text"),
                                sentAt = json.getLong("sent_at"),
                                expiresAt = json.getLong("expires_at"),
                                status = "SENT"
                            )
                            viewModelScope.launch { dao.insertMessage(msg) }
                        }
                        "expired" -> {
                            // Protocol 5: Server says delete it
                            val id = json.getString("id")
                            viewModelScope.launch { dao.deleteMessageById(id) }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Relay", "JSON Parse Error", e)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _isNetworkAvailable.value = false
                Log.e("Relay", "Connection Failed", t)
            }
        })
    }

    fun sendMessage(text: String, sender: String) {
        if (text.isBlank()) return
        val now = System.currentTimeMillis()

        if (webSocket != null && _isNetworkAvailable.value) {
            // Protocol 2: Send Message to Relay
            val id = UUID.randomUUID().toString()
            val msg = JSONObject().apply {
                put("type", "message")
                put("id", id)
                put("sender", sender)
                put("text", text)
            }
            webSocket?.send(msg.toString())
            // The server will echo this back to us, so we don't save it locally here.
        } else {
            // Offline Fallback (Stage 4)
            val newMessage = MessageEntity(
                id = UUID.randomUUID().toString(),
                sender = sender,
                text = text,
                sentAt = now,
                expiresAt = now + 30_000,
                status = "PENDING"
            )
            viewModelScope.launch { dao.insertMessage(newMessage) }
        }
    }
}