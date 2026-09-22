package Emeris.PROG7314.trinitymobileclient.ui.listeners

import Emeris.PROG7314.trinitymobileclient.R
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentListenersBinding
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment

//Listeners screen. UI shell only, no data logic here.
//Teammates: call showLoading/showError/showContent once the API is wired in.
class ListenersFragment : Fragment(R.layout.fragment_listeners) {

    //Cleared in onDestroyView. Getter, not eager init, that was the crash.
    private var _bindings: FragmentListenersBinding? = null
    private val bindings get() = _bindings!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        _bindings = FragmentListenersBinding.bind(view)

        //No data source yet, so open on the empty state.
        showEmpty()
    }

    //Request succeeded but there is nothing to show.
    fun showEmpty() {
        Log.d(TAG, "state -> empty")
        bindings.listenersContent.visibility = View.GONE
        bindings.stateListeners.showEmpty(
            icon = R.drawable.ic_listeners,
            title = R.string.state_listeners_empty_title,
            message = R.string.state_listeners_empty_message
        )
    }

    //A request is in flight.
    fun showLoading() {
        Log.d(TAG, "state -> loading")
        bindings.listenersContent.visibility = View.GONE
        bindings.stateListeners.showLoading()
    }

    //Request failed. Passing onRetry adds a Retry button.
    fun showError(onRetry: (() -> Unit)? = null) {
        Log.d(TAG, "state -> error")
        bindings.listenersContent.visibility = View.GONE
        bindings.stateListeners.showError(onRetry = onRetry)
    }

    //Data arrived, hide the state block and show the list.
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
