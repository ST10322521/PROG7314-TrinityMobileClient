package Emeris.PROG7314.trinitymobileclient.api

import Emeris.PROG7314.trinitymobileclient.api.service.AdminApiService
import Emeris.PROG7314.trinitymobileclient.api.service.AgentApiService
import Emeris.PROG7314.trinitymobileclient.api.service.ArmouryApiService
import Emeris.PROG7314.trinitymobileclient.api.service.AuthApiService
import Emeris.PROG7314.trinitymobileclient.api.service.CampaignApiService
import Emeris.PROG7314.trinitymobileclient.api.service.CommandApiService
import Emeris.PROG7314.trinitymobileclient.api.service.FilesApiService
import Emeris.PROG7314.trinitymobileclient.api.service.ListenerApiService
import Emeris.PROG7314.trinitymobileclient.api.service.OperatorApiService
import Emeris.PROG7314.trinitymobileclient.api.service.PayloadApiService
import Emeris.PROG7314.trinitymobileclient.api.service.ServerApiService
import Emeris.PROG7314.trinitymobileclient.api.service.TaskApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://urchin-app-uisgp.ondigitalocean.app/"

    var token: String? = null

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val request = chain.request().newBuilder().apply {
                    token?.let { addHeader("Authorization", "Bearer $it") }
                }.build()
                chain.proceed(request)
            }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val auth: AuthApiService     by lazy { retrofit.create(AuthApiService::class.java) }
    val agents: AgentApiService  by lazy { retrofit.create(AgentApiService::class.java) }
    val commands: CommandApiService by lazy { retrofit.create(CommandApiService::class.java) }
    val listeners: ListenerApiService by lazy { retrofit.create(ListenerApiService::class.java) }
    val tasks: TaskApiService    by lazy { retrofit.create(TaskApiService::class.java) }
    val payloads: PayloadApiService by lazy { retrofit.create(PayloadApiService::class.java) }
    val operators: OperatorApiService by lazy { retrofit.create(OperatorApiService::class.java) }
    val admins: AdminApiService  by lazy { retrofit.create(AdminApiService::class.java) }
    val campaigns: CampaignApiService by lazy { retrofit.create(CampaignApiService::class.java) }
    val armoury: ArmouryApiService by lazy { retrofit.create(ArmouryApiService::class.java) }
    val files: FilesApiService   by lazy { retrofit.create(FilesApiService::class.java) }
    val server: ServerApiService by lazy { retrofit.create(ServerApiService::class.java) }
}
