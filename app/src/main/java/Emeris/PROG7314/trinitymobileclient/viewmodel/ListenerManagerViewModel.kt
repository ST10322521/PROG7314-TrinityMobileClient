package Emeris.PROG7314.trinitymobileclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import Emeris.PROG7314.trinitymobileclient.api.NetworkManager
import Emeris.PROG7314.trinitymobileclient.api.model.HttpListenerDTO
import Emeris.PROG7314.trinitymobileclient.api.retrofit.ListenerApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

/** UI state for the listener manager. */
sealed interface ListenerUiState {
    data object Idle : ListenerUiState
    data object Working : ListenerUiState
    data class Ok(val message: String) : ListenerUiState
    data class Failed(val message: String) : ListenerUiState
}

/**
 * Listener manager (F2.2). Uses the OpenAPI-generated [ListenerApi]
 * (scripts/gen-openapi.sh) for every call — no hand-written HTTP here.
 *
 * NOTE: GET /api/v1/listeners is untyped (returns Unit) in the current spec, so the
 * list view renders reachability + local configuration, while create/delete actions
 * use the typed POST/DELETE endpoints.
 */
class ListenerManagerViewModel(
    private val listenerApi: ListenerApi = NetworkManager.listenerApi
) : ViewModel() {

    private val _state = MutableStateFlow<ListenerUiState>(ListenerUiState.Idle)
    val state: StateFlow<ListenerUiState> = _state.asStateFlow()

    fun createHttp(name: String, host: String, bindPort: Int) {
        if (host.isBlank()) {
            _state.value = ListenerUiState.Failed("Host cannot be empty")
            return
        }
        run("HTTP listener '$name' created") {
            listenerApi.apiV1ListenersHttpPost(
                HttpListenerDTO(
                    name = name.ifBlank { "http-listener" },
                    type = "http",
                    hosts = listOf(host),
                    httpBindPort = bindPort
                )
            )
        }
    }

    fun createTcp() = run("TCP listener created") { listenerApi.apiV1ListenersTcpPost() }

    fun deleteHttp(moduleId: String) {
        if (moduleId.isBlank()) {
            _state.value = ListenerUiState.Failed("Module id cannot be empty")
            return
        }
        run("HTTP listener '$moduleId' deleted") { listenerApi.apiV1ListenersHttpDelete(moduleId) }
    }

    private fun run(successMessage: String, block: suspend () -> Response<Unit>) {
        viewModelScope.launch {
            _state.value = ListenerUiState.Working
            try {
                val response = block()
                _state.value = if (response.isSuccessful) {
                    ListenerUiState.Ok(successMessage)
                } else {
                    ListenerUiState.Failed("Server returned HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = ListenerUiState.Failed(e.message ?: "Unable to reach server")
            }
        }
    }
}
