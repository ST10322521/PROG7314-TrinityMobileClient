package Emeris.PROG7314.trinitymobileclient.viewmodel

import Emeris.PROG7314.trinitymobileclient.api.model.BofDTO
import Emeris.PROG7314.trinitymobileclient.api.model.CancelDownloadDTO
import Emeris.PROG7314.trinitymobileclient.api.model.DotNetAsmDTO
import Emeris.PROG7314.trinitymobileclient.api.model.DownloadDTO
import Emeris.PROG7314.trinitymobileclient.api.model.EscalateDTO
import Emeris.PROG7314.trinitymobileclient.api.model.KillProcessDTO
import Emeris.PROG7314.trinitymobileclient.api.model.PowerShellDTO
import Emeris.PROG7314.trinitymobileclient.api.model.RunAsDTO
import Emeris.PROG7314.trinitymobileclient.api.model.RunDTO
import Emeris.PROG7314.trinitymobileclient.api.model.RunUDTO
import Emeris.PROG7314.trinitymobileclient.api.model.ShellDTO
import Emeris.PROG7314.trinitymobileclient.api.model.UploadDTO
import Emeris.PROG7314.trinitymobileclient.api.retrofit.CommandApi
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
class CommandConsoleViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    /** Counts how many shell commands were dispatched through the generated client. */
    private var shellDispatches = 0

    private val fakeApi = object : CommandApi {
        override suspend fun apiV1CommandsAgentIdExecuteBofPost(agentId: Int, bofDTO: BofDTO?): Response<Unit> = Response.success(Unit)
        override suspend fun apiV1CommandsAgentIdExecuteCancelFileDownloadPost(agentId: Int, cancelDownloadDTO: CancelDownloadDTO?): Response<Unit> = Response.success(Unit)
        override suspend fun apiV1CommandsAgentIdExecuteFiledownloadPost(agentId: Int, downloadDTO: DownloadDTO?): Response<Unit> = Response.success(Unit)
        override suspend fun apiV1CommandsAgentIdExecuteUploadPost(agentId: Int, uploadDTO: UploadDTO?): Response<Unit> = Response.success(Unit)
        override suspend fun apiV1CommandsAgentIdSpawnDotnetassemblyPost(agentId: Int, dotNetAsmDTO: DotNetAsmDTO?): Response<Unit> = Response.success(Unit)
        override suspend fun apiV1CommandsAgentIdSpawnEscalatePost(agentId: Int, escalateDTO: EscalateDTO?): Response<Unit> = Response.success(Unit)
        override suspend fun apiV1CommandsAgentIdSpawnKillprocessPost(agentId: Int, killProcessDTO: KillProcessDTO?): Response<Unit> = Response.success(Unit)
        override suspend fun apiV1CommandsAgentIdSpawnPowershellPost(agentId: Int, powerShellDTO: PowerShellDTO?): Response<Unit> = Response.success(Unit)
        override suspend fun apiV1CommandsAgentIdSpawnRunPost(agentId: Int, runDTO: RunDTO?): Response<Unit> = Response.success(Unit)
        override suspend fun apiV1CommandsAgentIdSpawnRunasPost(agentId: Int, runAsDTO: RunAsDTO?): Response<Unit> = Response.success(Unit)
        override suspend fun apiV1CommandsAgentIdSpawnRunuPost(agentId: Int, runUDTO: RunUDTO?): Response<Unit> = Response.success(Unit)
        override suspend fun apiV1CommandsAgentIdSpawnShellPost(agentId: Int, shellDTO: ShellDTO?): Response<Unit> {
            shellDispatches++
            return Response.success(Unit)
        }
    }

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test
    fun blank_command_is_rejected_without_calling_api() = runTest(dispatcher) {
        val vm = CommandConsoleViewModel(fakeApi)
        vm.send(1, CommandKind.SHELL, "   ")
        advanceUntilIdle()
        assertTrue(vm.state.value is CommandUiState.Failed)
        assertEquals(0, shellDispatches)
    }

    @Test
    fun shell_command_is_dispatched_via_generated_api() = runTest(dispatcher) {
        val vm = CommandConsoleViewModel(fakeApi)
        vm.send(42, CommandKind.SHELL, "whoami")
        advanceUntilIdle()
        assertTrue(vm.state.value is CommandUiState.Sent)
        assertEquals(1, shellDispatches)
    }
}
