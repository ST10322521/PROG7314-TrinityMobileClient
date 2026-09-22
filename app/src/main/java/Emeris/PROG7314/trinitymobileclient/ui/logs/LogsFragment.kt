package Emeris.PROG7314.trinitymobileclient.ui.logs

import Emeris.PROG7314.trinitymobileclient.R
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentLogsBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class LogsFragment : Fragment(R.layout.fragment_logs) {
    // Setting up bindings
    private var _bindings: FragmentLogsBinding? = null
    private val bindings = _bindings!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _bindings = FragmentLogsBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindings = null
    }
}
