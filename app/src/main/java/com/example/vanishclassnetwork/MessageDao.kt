package com.example.vanishclassnetwork

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY sentAt ASC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE status = 'PENDING'")
    suspend fun getPendingMessages(): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("UPDATE messages SET status = 'SENT' WHERE id = :messageId")
    suspend fun markAsSent(messageId: String)

    @Query("DELETE FROM messages WHERE expiresAt <= :currentTime")
    suspend fun deleteExpiredMessages(currentTime: Long)

    // NEW: Deletes a specific message by ID (used when server sends "expired")
    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessageById(id: String)
}