package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.databinding.FragmentListenersBinding
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment

// listeners screen, UI only. call the show* functions once the API is wired in
class ListenersFragment : Fragment(R.layout.fragment_listeners) {

    private var _bindings: FragmentListenersBinding? = null
    // getter not eager init, eager init crashed the app
    private val bindings get() = _bindings!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        _bindings = FragmentListenersBinding.bind(view)

        // TODO(connect to backend): no listeners come from the server yet so it starts empty
        showEmpty()
    }

    fun showEmpty() {
        Log.d(TAG, "state -> empty")
        bindings.listenersContent.visibility = View.GONE
        bindings.stateListeners.showEmpty(
            icon = R.drawable.ic_listeners,
            title = R.string.state_listeners_empty_title,
            message = R.string.state_listeners_empty_message
        )
    }

    fun showLoading() {
        Log.d(TAG, "state -> loading")
        bindings.listenersContent.visibility = View.GONE
        bindings.stateListeners.showLoading()
    }

    fun showError(onRetry: (() -> Unit)? = null) {
        Log.d(TAG, "state -> error")
        bindings.listenersContent.visibility = View.GONE
        bindings.stateListeners.showError(onRetry = onRetry)
    }

    fun showContent() {
        Log.d(TAG, "state -> content")
        bindings.stateListeners.showContent()
        bindings.listenersContent.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
        _bindings = null
    }

    private companion object {
        const val TAG = "ListenersFragment"
    }
}
