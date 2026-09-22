package Emeris.PROG7314.trinitymobileclient.ui.logs

import Emeris.PROG7314.trinitymobileclient.R
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentLogsBinding
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment

//Logs screen. UI shell only, no data logic here.
//Teammates: call showLoading/showError/showContent once the API is wired in.
class LogsFragment : Fragment(R.layout.fragment_logs) {

    //Cleared in onDestroyView. Getter, not eager init, that was the crash.
    private var _bindings: FragmentLogsBinding? = null
    private val bindings get() = _bindings!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        _bindings = FragmentLogsBinding.bind(view)

        //No data source yet, so open on the empty state.
        showEmpty()
    }

    //Request succeeded but there is nothing to show.
    fun showEmpty() {
        Log.d(TAG, "state -> empty")
        bindings.logsContent.visibility = View.GONE
        bindings.stateLogs.showEmpty(
            icon = R.drawable.ic_logs,
            title = R.string.state_logs_empty_title,
            message = R.string.state_logs_empty_message
        )
    }

    //A request is in flight.
    fun showLoading() {
        Log.d(TAG, "state -> loading")
        bindings.logsContent.visibility = View.GONE
        bindings.stateLogs.showLoading()
    }

    //Request failed. Passing onRetry adds a Retry button.
    fun showError(onRetry: (() -> Unit)? = null) {
        Log.d(TAG, "state -> error")
        bindings.logsContent.visibility = View.GONE
        bindings.stateLogs.showError(onRetry = onRetry)
    }

    //Data arrived, hide the state block and show the list.
    fun showContent() {
        Log.d(TAG, "state -> content")
        bindings.stateLogs.showContent()
        bindings.logsContent.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
        _bindings = null
    }

    private companion object {
        const val TAG = "LogsFragment"
    }
}
