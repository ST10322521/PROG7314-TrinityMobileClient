package Emeris.PROG7314.trinitymobileclient.ui.listenermanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import Emeris.PROG7314.trinitymobileclient.R
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentListenerManagerBinding
import Emeris.PROG7314.trinitymobileclient.viewmodel.ListenerManagerViewModel
import Emeris.PROG7314.trinitymobileclient.viewmodel.ListenerUiState
import kotlinx.coroutines.launch

/**
 * Listener Manager screen (F2.2). Separate package from the agent manager.
 */
class ListenerManagerFragment : Fragment(R.layout.fragment_listener_manager) {

    private var _binding: FragmentListenerManagerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListenerManagerViewModel by viewModels {
        viewModelFactory { initializer { ListenerManagerViewModel() } }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = FragmentListenerManagerBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListenerManagerBinding.bind(view)

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCreateHttp.setOnClickListener {
            val name = binding.inputName.text.toString().trim()
            val host = binding.inputHost.text.toString().trim()
            val port = binding.inputPort.text.toString().trim().toIntOrNull() ?: 80
            viewModel.createHttp(name, host, port)
        }

        binding.btnCreateTcp.setOnClickListener { viewModel.createTcp() }

        binding.btnDeleteHttp.setOnClickListener {
            viewModel.deleteHttp(binding.inputModuleId.text.toString().trim())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.listenerStatus.text = when (state) {
                    is ListenerUiState.Idle -> "Ready. Configure an HTTP or TCP listener."
                    is ListenerUiState.Working -> "Applying…"
                    is ListenerUiState.Ok -> "[ok] ${state.message}"
                    is ListenerUiState.Failed -> "[error] ${state.message}"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
