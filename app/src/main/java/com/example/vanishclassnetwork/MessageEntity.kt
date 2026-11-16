package com.example.vanishclassnetwork

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val sender: String,
    val text: String,
    val sentAt: Long,
    val expiresAt: Long,
    val status: String = "SENT" // "SENT" or "PENDING"
)