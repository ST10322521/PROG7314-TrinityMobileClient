package Emeris.PROG7314.trinitymobileclient.api

import Emeris.PROG7314.trinitymobileclient.api.infrastructure.ApiClient
import Emeris.PROG7314.trinitymobileclient.api.retrofit.AgentApi
import Emeris.PROG7314.trinitymobileclient.api.retrofit.CommandApi
import Emeris.PROG7314.trinitymobileclient.api.retrofit.ListenerApi
import Emeris.PROG7314.trinitymobileclient.api.retrofit.ServerApi
import Emeris.PROG7314.trinitymobileclient.api.retrofit.TaskApi
import android.util.Log

object NetworkManager {
    // Change this to your workstation IP if running on a physical device
    // Otherwise make sure to run `adb reverse tcp:5069 tcp:5069` on your workstation
    private const val BASE_URL = "http://localhost:5069/"

    private val apiClient: ApiClient by lazy {
        ApiClient(baseUrl = BASE_URL).apply {
            setLogger { message -> Log.d("ApiClient", message) }
        }
    }

    val serverApi: ServerApi by lazy { apiClient.createService(ServerApi::class.java) }
    val listenerApi: ListenerApi by lazy { apiClient.createService(ListenerApi::class.java) }
    val taskApi: TaskApi by lazy { apiClient.createService(TaskApi::class.java) }
    val commandApi: CommandApi by lazy { apiClient.createService(CommandApi::class.java) }
    val agentApi: AgentApi by lazy { apiClient.createService(AgentApi::class.java) }
}