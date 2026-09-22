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

/** A single listener row rendered by the design-16 listener list. */
data class ListenerRow(
    val id: Int,
    val name: String,
    val protocol: String,
    val bind: String,
    val agents: Int,
    val active: Boolean
)

/** UI state for the design-16 listener list. */
data class ListenerListState(
    val loading: Boolean,
    val rows: List<ListenerRow>,
    val total: Int,
    val activeCount: Int,
    val error: String?
)

/**
 * Listener list (F2.2) backed by the OpenAPI-generated [ListenerApi]
 * (scripts/gen-openapi.sh) — data always comes from the generated client.
 *
 * Endpoint used:
 *  - GET /api/v1/listeners
 */
class ListenerListViewModel(
    private val listenerApi: ListenerApi = NetworkManager.listenerApi
) : ViewModel() {

    private val _state = MutableStateFlow(
        ListenerListState(loading = true, rows = emptyList(), total = 0, activeCount = 0, error = null)
    )
    val state: StateFlow<ListenerListState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            try {
                val response = listenerApi.apiV1ListenersGet()
                if (response.isSuccessful) {
                    val rows = response.body().orEmpty().map { map(it) }
                    _state.value = ListenerListState(
                        loading = false,
                        rows = rows,
                        total = rows.size,
                        activeCount = rows.count { it.active },
                        error = null
                    )
                } else {
                    _state.value = ListenerListState(
                        loading = false,
                        rows = emptyList(),
                        total = 0,
                        activeCount = 0,
                        error = "Server returned HTTP ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _state.value = ListenerListState(
                    loading = false,
                    rows = emptyList(),
                    total = 0,
                    activeCount = 0,
                    error = e.message ?: "Unable to reach server"
                )
            }
        }
    }

    private fun map(d: HttpListenerDTO): ListenerRow = ListenerRow(
        id = d.id ?: 0,
        name = d.name ?: "listener",
        protocol = (d.type ?: "tcp").uppercase(),
        bind = if ((d.type ?: "").lowercase() == "http") {
            "${d.hosts?.firstOrNull() ?: "0.0.0.0"}:${d.httpBindPort ?: 0}"
        } else {
            "0.0.0.0:${d.httpC2BindPort ?: 0}"
        },
        agents = 0,
        active = true
    )
}
