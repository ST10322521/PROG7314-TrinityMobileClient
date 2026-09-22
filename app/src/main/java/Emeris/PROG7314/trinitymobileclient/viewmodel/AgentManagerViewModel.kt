package Emeris.PROG7314.trinitymobileclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import Emeris.PROG7314.trinitymobileclient.api.NetworkManager
import Emeris.PROG7314.trinitymobileclient.api.model.AgentDTO
import Emeris.PROG7314.trinitymobileclient.api.retrofit.AgentApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * UI state for agent screens backed by the generated [AgentApi] client.
 * The ViewModel never hand-builds API responses: data always comes from the
 * OpenAPI-generated service (scripts/gen-openapi.sh).
 */
sealed interface AgentUiState<out T> {
    data object Loading : AgentUiState<Nothing>
    data class Loaded<T>(val data: T) : AgentUiState<T>
    data class Failed(val message: String) : AgentUiState<Nothing>
}

/**
 * Agent list for the dashboard (GET /api/v1/agents).
 */
class AgentListViewModel(private val agentApi: AgentApi = NetworkManager.agentApi) : ViewModel() {

    private val _state = MutableStateFlow<AgentUiState<List<AgentDTO>>>(AgentUiState.Loading)
    val state: StateFlow<AgentUiState<List<AgentDTO>>> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.value = AgentUiState.Loading
            try {
                val response: Response<List<AgentDTO>> = agentApi.apiV1AgentsGet()
                _state.value = if (response.isSuccessful) {
                    AgentUiState.Loaded(response.body().orEmpty())
                } else {
                    AgentUiState.Failed("Server returned HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = AgentUiState.Failed(e.message ?: "Unable to reach server")
            }
        }
    }
}

/**
 * Agent detail for the agent interface screen (GET /api/v1/agents/{agentID}).
 */
class AgentDetailViewModel(private val agentApi: AgentApi = NetworkManager.agentApi) : ViewModel() {

    private val _state = MutableStateFlow<AgentUiState<AgentDTO>>(AgentUiState.Loading)
    val state: StateFlow<AgentUiState<AgentDTO>> = _state.asStateFlow()

    fun load(agentId: Int) {
        viewModelScope.launch {
            _state.value = AgentUiState.Loading
            try {
                val response: Response<AgentDTO> = agentApi.apiV1AgentsAgentIDGet(agentId)
                _state.value = if (response.isSuccessful) {
                    AgentUiState.Loaded(response.body() ?: AgentDTO())
                } else {
                    AgentUiState.Failed("Server returned HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = AgentUiState.Failed(e.message ?: "Unable to reach server")
            }
        }
    }
}
