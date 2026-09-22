package Emeris.PROG7314.trinitymobileclient.ui.listeners

import Emeris.PROG7314.trinitymobileclient.R
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentListenersBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class ListenersFragment : Fragment(R.layout.fragment_listeners) {
    // Setting up bindings
    private var _bindings: FragmentListenersBinding? = null
    private val bindings get() = _bindings!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _bindings = FragmentListenersBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindings = null
    }
}
