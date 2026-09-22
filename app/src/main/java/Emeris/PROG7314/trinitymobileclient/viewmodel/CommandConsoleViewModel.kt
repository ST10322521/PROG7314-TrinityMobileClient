package Emeris.PROG7314.trinitymobileclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import Emeris.PROG7314.trinitymobileclient.api.NetworkManager
import Emeris.PROG7314.trinitymobileclient.api.model.PowerShellDTO
import Emeris.PROG7314.trinitymobileclient.api.model.RunDTO
import Emeris.PROG7314.trinitymobileclient.api.model.ShellDTO
import Emeris.PROG7314.trinitymobileclient.api.retrofit.CommandApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

/** The command families the console can dispatch. */
enum class CommandKind { POWERSHELL, SHELL, RUN }

/** UI state for the agent command console. */
sealed interface CommandUiState {
    data object Idle : CommandUiState
    data object Sending : CommandUiState
    data class Sent(val message: String) : CommandUiState
    data class Failed(val message: String) : CommandUiState
}

/**
 * Agent command console (F2.3). All traffic goes through the OpenAPI-generated
 * [CommandApi] (scripts/gen-openapi.sh) — nothing is hand-rolled here.
 *
 * Endpoints used:
 *  - POST /api/v1/commands/{agentId}/spawn/powershell
 *  - POST /api/v1/commands/{agentId}/spawn/shell
 *  - POST /api/v1/commands/{agentId}/spawn/run
 */
class CommandConsoleViewModel(
    private val commandApi: CommandApi = NetworkManager.commandApi
) : ViewModel() {

    private val _state = MutableStateFlow<CommandUiState>(CommandUiState.Idle)
    val state: StateFlow<CommandUiState> = _state.asStateFlow()

    fun send(agentId: Int, kind: CommandKind, input: String) {
        if (input.isBlank()) {
            _state.value = CommandUiState.Failed("Command cannot be empty")
            return
        }
        viewModelScope.launch {
            _state.value = CommandUiState.Sending
            try {
                val response: Response<Unit> = when (kind) {
                    CommandKind.POWERSHELL ->
                        commandApi.apiV1CommandsAgentIdSpawnPowershellPost(agentId, PowerShellDTO(commandlet = input))
                    CommandKind.SHELL ->
                        commandApi.apiV1CommandsAgentIdSpawnShellPost(agentId, ShellDTO(command = input))
                    CommandKind.RUN ->
                        commandApi.apiV1CommandsAgentIdSpawnRunPost(agentId, RunDTO(program = input))
                }
                _state.value = if (response.isSuccessful) {
                    CommandUiState.Sent("${kind.name.lowercase()} queued for agent $agentId")
                } else {
                    CommandUiState.Failed("Server returned HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = CommandUiState.Failed(e.message ?: "Unable to reach server")
            }
        }
    }
}
