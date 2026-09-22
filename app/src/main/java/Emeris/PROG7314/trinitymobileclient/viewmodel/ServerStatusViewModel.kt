package Emeris.PROG7314.trinitymobileclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import Emeris.PROG7314.trinitymobileclient.api.NetworkManager
import kotlinx.coroutines.launch
import android.util.Log
import kotlin.coroutines.cancellation.CancellationException

class ServerStatusViewModel : ViewModel() {

    fun checkStatus() {
        viewModelScope.launch {
            try {
                val response = NetworkManager.serverApi.apiV1ServerStatusGet()
                if (response.isSuccessful) {
                    Log.d("API", "Server is reachable.")
                } else {
                    Log.e("API", "Error status: ${response.code()}")
                }
            } catch (e: CancellationException) {
                // Allow cancellation to propagate cleanly
                throw e
            } catch (e: Exception) {
                Log.e("API", "Connection failed", e)
            }
        }
    }
}