package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.databinding.FragmentPayloadsBinding
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment

//Payloads screen. UI shell only, no data logic here.
//Teammates: call showLoading/showError/showContent once the API is wired in.
class PayloadsFragment : Fragment(R.layout.fragment_payloads) {

    //Cleared in onDestroyView. Getter, not eager init, that was the crash.
    private var _bindings: FragmentPayloadsBinding? = null
    private val bindings get() = _bindings!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        _bindings = FragmentPayloadsBinding.bind(view)

        //No data source yet, so open on the empty state.
        showEmpty()
    }

    //Request succeeded but there is nothing to show.
    fun showEmpty() {
        Log.d(TAG, "state -> empty")
        bindings.payloadsContent.visibility = View.GONE
        bindings.statePayloads.showEmpty(
            icon = R.drawable.ic_file,
            title = R.string.state_payloads_empty_title,
            message = R.string.state_payloads_empty_message
        )
    }

    //A request is in flight.
    fun showLoading() {
        Log.d(TAG, "state -> loading")
        bindings.payloadsContent.visibility = View.GONE
        bindings.statePayloads.showLoading()
    }

    //Request failed. Passing onRetry adds a Retry button.
    fun showError(onRetry: (() -> Unit)? = null) {
        Log.d(TAG, "state -> error")
        bindings.payloadsContent.visibility = View.GONE
        bindings.statePayloads.showError(onRetry = onRetry)
    }

    //For example a payload finished generating.
    fun showSuccess() {
        Log.d(TAG, "state -> success")
        bindings.payloadsContent.visibility = View.GONE
        bindings.statePayloads.showSuccess()
    }

    //Data arrived, hide the state block and show the list.
    fun showContent() {
        Log.d(TAG, "state -> content")
        bindings.statePayloads.showContent()
        bindings.payloadsContent.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
        _bindings = null
    }

    private companion object {
        const val TAG = "PayloadsFragment"
    }
}
