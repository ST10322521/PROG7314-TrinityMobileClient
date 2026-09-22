package Emeris.PROG7314.trinitymobileclient.viewmodel

import Emeris.PROG7314.trinitymobileclient.api.model.AgentDTO
import Emeris.PROG7314.trinitymobileclient.api.retrofit.AgentApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private fun agent(id: Int, name: String) = AgentDTO(
    id = id,
    uuid = "uuid-$id",
    username = name,
    processName = "explorer.exe",
    integrity = 4,
    status = "active",
)

private class FakeAgentApi(
    private val agents: List<AgentDTO>? = null,
    private val listError: Int? = null,
    private val detailError: Int? = null,
) : AgentApi {
    override suspend fun apiV1AgentsGet(): Response<List<AgentDTO>> =
        if (listError != null) {
            Response.error(
                listError,
                "boom".toResponseBody("application/json".toMediaType())
            )
        } else {
            Response.success(agents.orEmpty())
        }

    override suspend fun apiV1AgentsAgentIDGet(agentID: Int): Response<AgentDTO> =
        if (detailError != null) {
            Response.error(
                detailError,
                "boom".toResponseBody("application/json".toMediaType())
            )
        } else {
            Response.success(agents?.first { it.id == agentID } ?: agent(agentID, "agent-$agentID"))
        }
}

private fun <T> AgentUiState<T>.requireLoaded(): AgentUiState.Loaded<T> = this as AgentUiState.Loaded<T>

/**
 * ViewModel tests. The fake [AgentApi] suspends nowhere, so the unconfined test
 * dispatcher drives [viewModelScope] to completion synchronously.
 */
class AgentManagerViewModelTest {

    private suspend fun TestScope.withMain(block: suspend () -> Unit) {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
        try {
            block()
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun list_loadsAgents() = runTest {
        withMain {
            val vm = AgentListViewModel(FakeAgentApi(listOf(agent(1, "agent-01"), agent(2, "agent-02"))))
            vm.load()
            val state = vm.state.value.requireLoaded()
            assertEquals(2, state.data.size)
            assertEquals("agent-01", state.data[0].username)
        }
    }

    @Test
    fun list_httpError_yieldsFailed() = runTest {
        withMain {
            val vm = AgentListViewModel(FakeAgentApi(listError = 500))
            vm.load()
            val state = vm.state.value as AgentUiState.Failed
            assertTrue(state.message.contains("500"))
        }
    }

    @Test
    fun detail_loadsAgentById() = runTest {
        withMain {
            val vm = AgentDetailViewModel(FakeAgentApi(listOf(agent(7, "agent-07"))))
            vm.load(7)
            val state = vm.state.value.requireLoaded()
            assertEquals(7, state.data.id)
            assertEquals("agent-07", state.data.username)
        }
    }

    @Test
    fun detail_httpError_yieldsFailed() = runTest {
        withMain {
            val vm = AgentDetailViewModel(FakeAgentApi(detailError = 404))
            vm.load(99)
            val state = vm.state.value as AgentUiState.Failed
            assertTrue(state.message.contains("404"))
        }
    }
}
