package Emeris.PROG7314.trinitymobileclient.ui.settings

import Emeris.PROG7314.trinitymobileclient.R
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentSettingsBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    // Setting up bindings
    private var _bindings: FragmentSettingsBinding? = null
    private val bindings get() = _bindings!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _bindings = FragmentSettingsBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindings = null
    }
}

