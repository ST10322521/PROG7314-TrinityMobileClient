You are in /tmp/restore/wt-f22, a git worktree of the Android app TrinityMobileClient (branch feature/listener-manager).
TASK: create exactly ONE new file and commit it:
  app/src/main/java/Emeris/PROG7314/trinitymobileclient/viewmodel/ListenerListViewModel.kt

SPEC (follow exactly):
package Emeris.PROG7314.trinitymobileclient.viewmodel

Imports you need (read these files first to confirm signatures):
- Emeris.PROG7314.trinitymobileclient.api.NetworkManager   (use NetworkManager.listenerApi)
- Emeris.PROG7314.trinitymobileclient.api.retrofit.ListenerApi
- Emeris.PROG7314.trinitymobileclient.api.model.HttpListenerDTO

content:
  data class ListenerRow(val id: Int, val name: String, val protocol: String, val bind: String, val agents: Int, val active: Boolean)
  data class ListenerListState(val loading: Boolean, val rows: List<ListenerRow>, val total: Int, val activeCount: Int, val error: String?)
  class ListenerListViewModel(private val listenerApi: ListenerApi = NetworkManager.listenerApi) : ViewModel()
    - private val _state = MutableStateFlow(ListenerListState(loading = true, rows = emptyList(), total = 0, activeCount = 0, error = null))
    - val state: StateFlow<ListenerListState> = _state.asStateFlow()
    - fun load(): viewModelScope.launch { set loading=true; try { val r = listenerApi.apiV1ListenersGet(); if (r.isSuccessful) { val rows = r.body().orEmpty().map { map(it) }; _state.value = ListenerListState(false, rows, rows.size, rows.count{it.active}, null) } else _state.value = ListenerListState(false, emptyList(), 0, 0, "Server returned HTTP ${r.code()}") } catch (e: Exception) { _state.value = ListenerListState(false, emptyList(), 0, 0, e.message ?: "Unable to reach server") } }
    - private fun map(d: HttpListenerDTO): ListenerRow  =>  ListenerRow(
          id = d.id ?: 0,
          name = d.name ?: "listener",
          protocol = (d.type ?: "tcp").uppercase(),
          bind = if ((d.type ?: "").lowercase() == "http") "${d.hosts?.firstOrNull() ?: "0.0.0.0"}:${d.httpBindPort ?: 0}" else "0.0.0.0:${d.httpC2BindPort ?: 0}",
          agents = 0,
          active = true)
  Use MutableStateFlow/StateFlow (kotlinx.coroutines.flow), ViewModel, viewModelScope.

STYLE: mirror viewmodel/CommandConsoleViewModel.kt and viewmodel/AgentManagerViewModel.kt.
RULES: Kotlin only. Do not touch any other file. It MUST compile:
  export JAVA_HOME=/mnt/shared/toolchains/jdk17
  ./gradlew :app:assembleDebug --console=plain     # fix until BUILD SUCCESSFUL
Then: git add <the file> && git commit -m "feat(listener-manager): ListenerListViewModel for design-16 list (F2.2)"
NEVER push. When done print exactly: F22-VM-DONE
