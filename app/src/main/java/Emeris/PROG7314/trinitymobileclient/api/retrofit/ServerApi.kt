package Emeris.PROG7314.trinitymobileclient.api.retrofit

import Emeris.PROG7314.trinitymobileclient.api.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName


interface ServerApi {
    /**
     * GET api/v1/server/teamserverip
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Unit]
     */
    @GET("api/v1/server/teamserverip")
    suspend fun getServerIP(): Response<Unit>

    /**
     * GET api/v1/server/status
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Unit]
     */
    @GET("api/v1/server/status")
    suspend fun getServerStatus(): Response<Unit>

}
