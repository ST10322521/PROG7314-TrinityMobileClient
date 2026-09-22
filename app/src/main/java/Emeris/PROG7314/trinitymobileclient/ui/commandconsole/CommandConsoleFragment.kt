package Emeris.PROG7314.trinitymobileclient.ui.commandconsole

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
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentCommandConsoleBinding
import Emeris.PROG7314.trinitymobileclient.viewmodel.CommandConsoleViewModel
import Emeris.PROG7314.trinitymobileclient.viewmodel.CommandKind
import Emeris.PROG7314.trinitymobileclient.viewmodel.CommandUiState
import kotlinx.coroutines.launch

/**
 * Agent Command Console screen (F2.3). Separate package from the agent manager.
 */
class CommandConsoleFragment : Fragment(R.layout.fragment_command_console) {

    private var _binding: FragmentCommandConsoleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CommandConsoleViewModel by viewModels {
        viewModelFactory { initializer { CommandConsoleViewModel() } }
    }

    private var kind: CommandKind = CommandKind.POWERSHELL

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = FragmentCommandConsoleBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCommandConsoleBinding.bind(view)

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.chipPowershell.setOnClickListener { kind = CommandKind.POWERSHELL; highlight() }
        binding.chipShell.setOnClickListener { kind = CommandKind.SHELL; highlight() }
        binding.chipRun.setOnClickListener { kind = CommandKind.RUN; highlight() }
        highlight()

        binding.btnSend.setOnClickListener {
            val agentId = binding.inputAgentId.text.toString().trim().toIntOrNull()
            if (agentId == null) {
                binding.consoleOutput.text = "[error] Enter a valid numeric agent ID"
                return@setOnClickListener
            }
            viewModel.send(agentId, kind, binding.inputCommand.text.toString().trim())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.consoleOutput.text = when (state) {
                    is CommandUiState.Idle -> "Ready. Select a channel and dispatch a command."
                    is CommandUiState.Sending -> "Dispatching…"
                    is CommandUiState.Sent -> "[ok] ${state.message}"
                    is CommandUiState.Failed -> "[error] ${state.message}"
                }
            }
        }
    }

    private fun highlight() {
        val active = 0xFFFF6A00.toInt()
        val idle = 0xFF9AA0A6.toInt()
        binding.chipPowershell.setTextColor(if (kind == CommandKind.POWERSHELL) active else idle)
        binding.chipShell.setTextColor(if (kind == CommandKind.SHELL) active else idle)
        binding.chipRun.setTextColor(if (kind == CommandKind.RUN) active else idle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
