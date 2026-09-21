package Emeris.PROG7314.trinitymobileclient.api.retrofit

import Emeris.PROG7314.trinitymobileclient.api.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import Emeris.PROG7314.trinitymobileclient.api.model.HttpListenerDTO
import Emeris.PROG7314.trinitymobileclient.api.model.HttpListenerDto

interface ListenerApi {
    /**
     * GET api/v1/listeners
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Unit]
     */
    @GET("api/v1/listeners")
    suspend fun apiV1ListenersGet(): Response<Unit>

    /**
     * DELETE api/v1/listeners/http
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param moduleId  (optional)
     * @return [Unit]
     */
    @DELETE("api/v1/listeners/http")
    suspend fun apiV1ListenersHttpDelete(@Query("moduleId") moduleId: kotlin.String? = null): Response<Unit>

    /**
     * POST api/v1/listeners/http
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param httpListenerDTO  (optional)
     * @return [Unit]
     */
    @POST("api/v1/listeners/http")
    suspend fun apiV1ListenersHttpPost(@Body httpListenerDTO: HttpListenerDTO? = null): Response<Unit>

    /**
     * PUT api/v1/listeners/http
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param httpListenerDto  (optional)
     * @return [Unit]
     */
    @PUT("api/v1/listeners/http")
    suspend fun apiV1ListenersHttpPut(@Body httpListenerDto: HttpListenerDto? = null): Response<Unit>

    /**
     * POST api/v1/listeners/tcp
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Unit]
     */
    @POST("api/v1/listeners/tcp")
    suspend fun apiV1ListenersTcpPost(): Response<Unit>

}
