package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.databinding.FragmentLogsBinding
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment

// logs screen, UI only. call the show* functions once the API is wired in
class LogsFragment : Fragment(R.layout.fragment_logs) {

    private var _bindings: FragmentLogsBinding? = null
    // getter not eager init, eager init crashed the app
    private val bindings get() = _bindings!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        _bindings = FragmentLogsBinding.bind(view)

        // TODO(connect to backend): no logs come from the server yet so it starts empty
        showEmpty()
    }

    fun showEmpty() {
        Log.d(TAG, "state -> empty")
        bindings.logsContent.visibility = View.GONE
        bindings.stateLogs.showEmpty(
            icon = R.drawable.ic_logs,
            title = R.string.state_logs_empty_title,
            message = R.string.state_logs_empty_message
        )
    }

    fun showLoading() {
        Log.d(TAG, "state -> loading")
        bindings.logsContent.visibility = View.GONE
        bindings.stateLogs.showLoading()
    }

    fun showError(onRetry: (() -> Unit)? = null) {
        Log.d(TAG, "state -> error")
        bindings.logsContent.visibility = View.GONE
        bindings.stateLogs.showError(onRetry = onRetry)
    }

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
