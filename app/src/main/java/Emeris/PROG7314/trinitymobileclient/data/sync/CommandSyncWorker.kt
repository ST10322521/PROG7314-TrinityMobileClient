package Emeris.PROG7314.trinitymobileclient.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import Emeris.PROG7314.trinitymobileclient.api.ApiClient
import Emeris.PROG7314.trinitymobileclient.data.CommandRepository
import Emeris.PROG7314.trinitymobileclient.data.command.dto.CmdDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.DownloadDTO
import Emeris.PROG7314.trinitymobileclient.data.command.dto.EscalateDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.GetUIDDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.InlineExecuteDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.KillDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.KillProcessDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.PersistDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.PowerShellDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.RunAsDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.RunDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.RunUDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.SleepDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.SpawnToDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.UpdateHostsDto
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

        for (command in pending) {repository.markSending(command.id)

            try {
                val agentId = command.agentId.toIntOrNull()
                    ?: throw IllegalArgumentException( "Invalid agent ID '${command.agentId}'" )

                val response = when (command.commandType) {
                    CommandType.POWERSHELL ->
                        ApiClient.commands.powershell(
                            agentId,
                            repository.deserialize(command.payload, PowerShellDto::class.java)
                        )

                    CommandType.SHELL ->
                        ApiClient.commands.shell(
                            agentId,
                            repository.deserialize(command.payload, CmdDto::class.java)
                        )

                    CommandType.RUNAS ->
                        ApiClient.commands.runAs(
                            agentId,
                            repository.deserialize(command.payload, RunAsDto::class.java)
                        )

                    CommandType.RUN ->
                        ApiClient.commands.run(
                            agentId,
                            repository.deserialize(command.payload, RunDto::class.java)
                        )

                    CommandType.RUNU ->
                        ApiClient.commands.runU(
                            agentId,
                            repository.deserialize(command.payload, RunUDto::class.java)
                        )

                    CommandType.PERSIST ->
                        ApiClient.commands.persist(
                            agentId,
                            repository.deserialize(command.payload, PersistDto::class.java)
                        )

                    CommandType.KILL_PROCESS ->
                        ApiClient.commands.killProcess(
                            agentId,
                            repository.deserialize(command.payload, KillProcessDto::class.java)
                        )

                    CommandType.ESCALATE ->
                        ApiClient.commands.escalate(
                            agentId,
                            repository.deserialize(command.payload, EscalateDto::class.java)
                        )

                    CommandType.EXECUTE_ASSEMBLY ->
                        ApiClient.commands.dotNetAssembly(
                            agentId,
                            repository.deserialize(command.payload, InlineExecuteDto::class.java)
                        )

                    CommandType.DOWNLOAD ->
                        ApiClient.commands.fileDownload(
                            agentId,
                            repository.deserialize(command.payload, DownloadDTO::class.java)
                        )

                    CommandType.SPAWNTO ->
                        ApiClient.commands.spawnTo(
                            agentId,
                            repository.deserialize(command.payload, SpawnToDto::class.java)
                        )

                    CommandType.UPDATE_HOSTS ->
                        ApiClient.commands.updateHosts(
                            agentId,
                            repository.deserialize(command.payload, UpdateHostsDto::class.java)
                        )

                    CommandType.SLEEP ->
                        ApiClient.commands.sleep(
                            agentId,
                            repository.deserialize(command.payload, SleepDto::class.java)
                        )

                    CommandType.KILL ->
                        ApiClient.commands.kill(
                            agentId,
                            repository.deserialize(command.payload, KillDto::class.java)
                        )

                    CommandType.GETUID ->
                        ApiClient.commands.getUid(
                            agentId,
                            repository.deserialize(command.payload, GetUIDDto::class.java)
                        )

                    else -> {
                        throw IllegalArgumentException( "Unknown command type '${command.commandType}'" )
                    }
                }

                if (!response.isSuccessful) {
                    throw IllegalStateException( "Server rejected command ${command.id}: HTTP ${response.code()}" )
                }

                repository.markSent(command.id)
                Log.d(TAG, "Command ${command.id} sent successfully")

            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Failed to send command ${command.id} " +
                            "(type=${command.commandType})",
                    e
                )
                repository.markFailed(command.id)
                anyFailed = true
            }
        }

        repository.clearSent()

        return if (anyFailed) Result.retry() else Result.success()
    }
}