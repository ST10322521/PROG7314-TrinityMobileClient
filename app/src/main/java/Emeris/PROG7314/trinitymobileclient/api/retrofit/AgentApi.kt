package Emeris.PROG7314.trinitymobileclient.api.retrofit

import Emeris.PROG7314.trinitymobileclient.api.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import Emeris.PROG7314.trinitymobileclient.api.model.AgentDTO

interface AgentApi {
    /**
     * GET api/v1/agents/{agentID}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param agentID 
     * @return [AgentDTO]
     */
    @GET("api/v1/agents/{agentID}")
    suspend fun apiV1AgentsAgentIDGet(@Path("agentID") agentID: kotlin.Int): Response<AgentDTO>

    /**
     * GET api/v1/agents
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [kotlin.collections.List<AgentDTO>]
     */
    @GET("api/v1/agents")
    suspend fun apiV1AgentsGet(): Response<kotlin.collections.List<AgentDTO>>

}
