package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.databinding.FragmentLogsBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class LogsFragment : Fragment(R.layout.fragment_logs) {
    // Setting up bindings
    private var _bindings: FragmentLogsBinding? = null
    private val bindings get() = _bindings!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _bindings = FragmentLogsBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindings = null
    }
}
