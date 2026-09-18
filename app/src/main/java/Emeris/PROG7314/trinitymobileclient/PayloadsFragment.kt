package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.databinding.FragmentPayloadsBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class PayloadsFragment : Fragment(R.layout.fragment_payloads) {
    // Setting up bindings
    private var _bindings: FragmentPayloadsBinding? = null
    private val bindings = _bindings!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _bindings = FragmentPayloadsBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindings = null
    }
}
