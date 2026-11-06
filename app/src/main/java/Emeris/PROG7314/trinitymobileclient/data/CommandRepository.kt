package Emeris.PROG7314.trinitymobileclient.data

import android.content.Context
import com.google.gson.Gson
import Emeris.PROG7314.trinitymobileclient.data.db.CommandDatabase
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandStatus
import Emeris.PROG7314.trinitymobileclient.data.db.entity.PendingCommand

/**
 * Public API for the offline command queue.
 */
class CommandRepository(context: Context) {

    private val dao  = CommandDatabase.getInstance(context).commandDao()
    private val gson = Gson()

    // ── Fragment: queue a command ─────────────────────────────────────────────

    /**
     * Serialize [dto] to JSON and store it as a PENDING command.
     *
     * @param agentId     The target agent ID
     * @param commandType Discriminator string — use constants from [CommandType]
     * @param dto         Command DTO;
     * @return            The database row ID of the queued command
     */
    suspend fun <T : Any> queueCommand(
        agentId: String,
        commandType: String,
        dto: T
    ): Long {
        val payload = gson.toJson(dto)
        return dao.insert(
            PendingCommand(
                agentId     = agentId,
                commandType = commandType,
                payload     = payload
            )
        )
    }

    /** PENDING commands for a specific agent, oldest first. */
    suspend fun getPendingForAgent(agentId: String): List<PendingCommand> =
        dao.getPendingForAgent(agentId)

    /** All PENDING commands across every agent, oldest first. */
    suspend fun getAllPending(): List<PendingCommand> =
        dao.getAllPending()

    /** All FAILED commands available for retry. */
    suspend fun getAllFailed(): List<PendingCommand> =
        dao.getAllFailed()

    /** Call before sending  */
    suspend fun markSending(id: Long)  = dao.markSending(id)

    /** Call after the endpoint confirms receipt. */
    suspend fun markSent(id: Long)     = dao.markSent(id)

    /** Call if the send fails. Increments the retry counter. */
    suspend fun markFailed(id: Long)   = dao.markFailed(id)

    /** Re-queue a failed command for another attempt. */
    suspend fun retry(id: Long)        = dao.resetToPending(id)
    suspend fun clearSent()            = dao.clearSent()
    fun <T> deserialize(payload: String, type: Class<T>): T =
        gson.fromJson(payload, type)
}
