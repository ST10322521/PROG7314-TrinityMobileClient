package Emeris.PROG7314.trinitymobileclient.viewmodel

import Emeris.PROG7314.trinitymobileclient.api.model.HttpListenerDTO
import Emeris.PROG7314.trinitymobileclient.api.model.HttpListenerDto
import Emeris.PROG7314.trinitymobileclient.api.retrofit.ListenerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ListenerManagerViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private var httpCreated = 0
    private var lastHost: String? = null

    private val fakeApi = object : ListenerApi {
        override suspend fun apiV1ListenersGet(): Response<List<HttpListenerDTO>> = Response.success(emptyList())
        override suspend fun apiV1ListenersHttpDelete(moduleId: String?): Response<Unit> = Response.success(Unit)
        override suspend fun apiV1ListenersHttpPost(httpListenerDTO: HttpListenerDTO?): Response<Unit> {
            httpCreated++
            lastHost = httpListenerDTO?.hosts?.firstOrNull()
            return Response.success(Unit)
        }
        override suspend fun apiV1ListenersHttpPut(httpListenerDto: HttpListenerDto?): Response<Unit> = Response.success(Unit)
        override suspend fun apiV1ListenersTcpPost(): Response<Unit> = Response.success(Unit)
    }

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test
    fun blank_host_is_rejected() = runTest(dispatcher) {
        val vm = ListenerManagerViewModel(fakeApi)
        vm.createHttp("listener", "", 8080)
        advanceUntilIdle()
        assertTrue(vm.state.value is ListenerUiState.Failed)
        assertEquals(0, httpCreated)
    }

    @Test
    fun http_listener_is_created_with_host() = runTest(dispatcher) {
        val vm = ListenerManagerViewModel(fakeApi)
        vm.createHttp("primary", "10.0.0.5", 8080)
        advanceUntilIdle()
        assertTrue(vm.state.value is ListenerUiState.Ok)
        assertEquals(1, httpCreated)
        assertEquals("10.0.0.5", lastHost)
    }
}
