package Emeris.PROG7314.trinitymobileclient.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a command queued in the agent terminal while offline (or online).
 */
@Entity(tableName = "pending_commands")
data class PendingCommand(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** The agent this command is destined for. */
    val agentId: String,

    /**
     * Discriminator string that identifies the DTO shape stored in [payload].
     * Define as constants in [CommandType] to avoid magic strings.
     */
    val commandType: String,

    /**
     * The full serialized command DTO as a JSON string.
     */
    val payload: String,
    val status: CommandStatus = CommandStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),

    /** Incremented each time the sender retries after a failure. */
    val retryCount: Int = 0
)

/**
 * Lifecycle states for a queued command.
 *
 * PENDING  → queued, not yet picked up by the sender
 * SENDING  → currently being sent to the endpoint
 * SENT     → successfully delivered
 * FAILED   → all retries exhausted
 */
enum class CommandStatus {
    PENDING,
    SENDING,
    SENT,
    FAILED
}

/**
 * Constant command types
 */
object CommandType {
    const val POWERSHELL       = "powershell"
    const val SHELL            = "shell"
    const val DOWNLOAD         = "download"
    const val KILL_PROCESS     = "kill-process"
    const val RUNAS            = "runas"
    const val RUN              = "run"
    const val RUNU             = "runu"
    const val ESCALATE         = "escalate"
    const val PERSIST          = "persist"
    const val EXECUTE_ASSEMBLY = "execute-assembly"
    const val SPAWNTO          = "spawnto"
    const val UPDATE_HOSTS     = "updatehosts"
    const val SLEEP            = "sleep"
    const val KILL             = "kill"
    const val GETUID           = "getuid"
}
