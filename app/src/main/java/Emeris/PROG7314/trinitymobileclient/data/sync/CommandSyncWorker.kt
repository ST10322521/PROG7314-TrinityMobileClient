package Emeris.PROG7314.trinitymobileclient.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import Emeris.PROG7314.trinitymobileclient.data.CommandRepository
import Emeris.PROG7314.trinitymobileclient.data.command.dto.ShellCommandDto
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

/**
 * Background worker that runs when the device has an active internet connection.
 */
class CommandSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "CommandSyncWorker"
    }

    private val repository = CommandRepository(applicationContext)

    override suspend fun doWork(): Result {
        Log.d(TAG, "Sync started — fetching pending commands")

        val pending = repository.getAllPending()

        if (pending.isEmpty()) {
            Log.d(TAG, "No pending commands — nothing to do")
            return Result.success()
        }
        Log.d(TAG, "Found ${pending.size} pending command(s)")

        var anyFailed = false

        for (command in pending) {
            repository.markSending(command.id)

            try {
                when (command.commandType) {

                    CommandType.SHELL -> {
                        val dto = repository.deserialize(command.payload, ShellCommandDto::class.java)
                        Log.d(TAG, "Dispatching shell to agent '${command.agentId}': ${dto.commandlet}")
                        // TODO: ADD API CALL
                    }
                    else -> {
                        Log.w(TAG, "Unknown command type '${command.commandType}' (id=${command.id}) — skipping")
                        repository.markFailed(command.id)
                        anyFailed = true
                        continue
                    }
                }
                repository.markSent(command.id)
                Log.d(TAG, "Command ${command.id} marked sent")

            } catch (e: Exception) {
                Log.e(TAG, "Failed to send command ${command.id} (type=${command.commandType})", e)
                repository.markFailed(command.id)
                anyFailed = true
            }
        }

        repository.clearSent()

        return if (anyFailed) Result.retry() else Result.success()
    }
}
