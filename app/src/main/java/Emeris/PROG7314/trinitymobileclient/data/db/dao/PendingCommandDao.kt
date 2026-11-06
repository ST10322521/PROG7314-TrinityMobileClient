package Emeris.PROG7314.trinitymobileclient.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandStatus
import Emeris.PROG7314.trinitymobileclient.data.db.entity.PendingCommand

@Dao
interface PendingCommandDao {

    // Persist commands
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(command: PendingCommand): Long

    // Read stored commands
    @Query("SELECT * FROM pending_commands WHERE agentId = :agentId AND status = 'PENDING' ORDER BY createdAt ASC")
    suspend fun getPendingForAgent(agentId: String): List<PendingCommand>
    @Query("SELECT * FROM pending_commands WHERE status = 'PENDING' ORDER BY createdAt ASC")
    suspend fun getAllPending(): List<PendingCommand>
    @Query("SELECT * FROM pending_commands WHERE status = 'FAILED' ORDER BY createdAt ASC")
    suspend fun getAllFailed(): List<PendingCommand>

    // Status updates
    @Query("UPDATE pending_commands SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: CommandStatus)
    @Query("UPDATE pending_commands SET status = 'SENDING' WHERE id = :id")
    suspend fun markSending(id: Long)
    @Query("UPDATE pending_commands SET status = 'SENT' WHERE id = :id")
    suspend fun markSent(id: Long)
    @Query("UPDATE pending_commands SET retryCount = retryCount + 1, status = 'FAILED' WHERE id = :id")
    suspend fun markFailed(id: Long)
    @Query("UPDATE pending_commands SET status = 'PENDING' WHERE id = :id")
    suspend fun resetToPending(id: Long)
    @Query("DELETE FROM pending_commands WHERE status = 'SENT'")
    suspend fun clearSent()
}
