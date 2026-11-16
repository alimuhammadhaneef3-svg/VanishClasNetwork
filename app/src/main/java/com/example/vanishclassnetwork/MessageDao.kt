package com.example.vanishclassnetwork

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    // Gets all messages, ordered by oldest to newest
    @Query("SELECT * FROM messages ORDER BY sentAt ASC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    // Gets only messages that are waiting to be sent (Offline Queue)
    @Query("SELECT * FROM messages WHERE status = 'PENDING'")
    suspend fun getPendingMessages(): List<MessageEntity>

    // Inserts a message
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(message: MessageEntity)

    // Marks a message as successfully sent when network returns
    @Query("UPDATE messages SET status = 'SENT' WHERE id = :messageId")
    suspend fun markAsSent(messageId: String)

    // DELETES messages from the database where the expiry time has passed
    @Query("DELETE FROM messages WHERE expiresAt <= :currentTime")
    suspend fun deleteExpiredMessages(currentTime: Long)
}