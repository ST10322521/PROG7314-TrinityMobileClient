package Emeris.PROG7314.trinitymobileclient.ui.agentmanager

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import Emeris.PROG7314.trinitymobileclient.R
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentAgentInterfaceBinding
import Emeris.PROG7314.trinitymobileclient.model.Agent
import Emeris.PROG7314.trinitymobileclient.model.toUiAgent
import Emeris.PROG7314.trinitymobileclient.viewmodel.AgentDetailViewModel
import Emeris.PROG7314.trinitymobileclient.viewmodel.AgentUiState
import Emeris.PROG7314.trinitymobileclient.viewmodel.CommandConsoleViewModel
import Emeris.PROG7314.trinitymobileclient.viewmodel.CommandKind
import Emeris.PROG7314.trinitymobileclient.viewmodel.CommandUiState
import kotlinx.coroutines.launch

/**
 * Agent interface drill-down (F2.1 detail + F2.3 console), matched to Design.pdf design-15:
 * endpoint summary, an EMBEDDED command console, Settings actions and the Agent Tasks
 * section. The console and every Settings action are driven by the OpenAPI-generated
 * [Emeris.PROG7314.trinitymobileclient.api.retrofit.CommandApi] via [CommandConsoleViewModel]
 * (POST /api/v1/commands/{agentId}/spawn/{shell|powershell|run}).
 */
class AgentInterfaceFragment : Fragment(R.layout.fragment_agent_interface) {

    private var _binding: FragmentAgentInterfaceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AgentDetailViewModel by viewModels {
        viewModelFactory { initializer { AgentDetailViewModel() } }
    }

    private val consoleViewModel: CommandConsoleViewModel by viewModels {
        viewModelFactory { initializer { CommandConsoleViewModel() } }
    }

    private var agentId: Int = -1
    private val transcript = StringBuilder()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = FragmentAgentInterfaceBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAgentInterfaceBinding.bind(view)

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        agentId = arguments?.getInt(ARG_AGENT_ID) ?: -1
        if (agentId >= 0) viewModel.load(agentId)

        binding.inputCommand.hint = "Enter command for AGENT-${if (agentId >= 0) agentId else 1}…"
        binding.btnSendCommand.setOnClickListener { sendConsoleCommand() }
        binding.btnClearConsole.setOnClickListener {
            transcript.clear(); binding.consoleOutput.text = ""
        }

        // Settings actions (design-15): all dispatch through the generated CommandApi.
        binding.btnKill.setOnClickListener { confirmKill() }
        binding.btnSleep.setOnClickListener {
            prompt("Set sleep (seconds)", "60") { sendShell("sleep $it") }
        }
        binding.btnSpawnto.setOnClickListener {
            prompt("Set spawn-to process", "C:\\Windows\\System32\\svchost.exe") { sendRun(it) }
        }
        binding.btnUpdateHosts.setOnClickListener {
            prompt("Update hosts entry", "10.0.0.5 trinity.local") { sendShell("hosts-update $it") }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is AgentUiState.Loading -> binding.agentTitle.text = "Loading…"
                    is AgentUiState.Failed -> binding.agentTitle.text = "Error: ${state.message}"
                    is AgentUiState.Loaded -> renderAgent(state.data.toUiAgent())
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            consoleViewModel.state.collect { appendConsole(it) }
        }

        seedConsole()
    }

    private fun sendConsoleCommand() {
        val cmd = binding.inputCommand.text.toString().trim()
        if (cmd.isEmpty()) return
        transcript.append("\n> ").append(cmd).append('\n')
        dispatch(CommandKind.SHELL, cmd)
        binding.inputCommand.setText("")
        binding.consoleOutput.text = transcript.toString()
    }

    private fun sendShell(cmd: String) {
        transcript.append("\n> ").append(cmd).append('\n')
        dispatch(CommandKind.SHELL, cmd)
        binding.consoleOutput.text = transcript.toString()
    }

    private fun sendRun(program: String) {
        transcript.append("\n> run ").append(program).append('\n')
        dispatch(CommandKind.RUN, program)
        binding.consoleOutput.text = transcript.toString()
    }

    private fun dispatch(kind: CommandKind, input: String) {
        if (agentId < 0) {
            transcript.append("[error] no agent selected\n")
            return
        }
        consoleViewModel.send(agentId, kind, input)
    }

    private fun appendConsole(state: CommandUiState) {
        when (state) {
            is CommandUiState.Sent -> transcript.append(state.message).append('\n')
            is CommandUiState.Failed -> transcript.append("[error] ").append(state.message).append('\n')
            is CommandUiState.Sending, is CommandUiState.Idle -> Unit
        }
        _binding?.consoleOutput?.text = transcript.toString()
    }

    private fun seedConsole() {
        transcript.append("Trinity C2 operator console\n")
        transcript.append("target: AGENT-").append(if (agentId >= 0) agentId else 1).append('\n')
        transcript.append("channels: shell · powershell · run\n")
        binding.consoleOutput.text = transcript.toString()
    }

    private fun prompt(title: String, initial: String, onOk: (String) -> Unit) {
        val ctx = requireContext()
        val input = EditText(ctx).apply { setText(initial) }
        AlertDialog.Builder(ctx)
            .setTitle(title)
            .setView(input)
            .setPositiveButton("Send") { _, _ ->
                val v = input.text.toString().trim()
                if (v.isNotEmpty()) onOk(v)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmKill() {
        AlertDialog.Builder(requireContext())
            .setTitle("Kill agent")
            .setMessage("Send a kill command to AGENT-$agentId?")
            .setPositiveButton("Kill") { _, _ ->
                transcript.append("\n> kill\n")
                dispatch(CommandKind.SHELL, "kill")
                binding.consoleOutput.text = transcript.toString()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun renderAgent(agent: Agent) {
        binding.agentTitle.text = agent.name
        binding.agentSubtitle.text = "Campaign ${agent.campaignId ?: "—"} · Agent Interface"
        binding.agentStatus.text = agent.status.ifEmpty { "—" }
        binding.agentProcess.text = agent.processName.ifEmpty { "—" }
        binding.agentIntegrity.text = agent.integrity?.toString() ?: "—"
        binding.agentSleep.text = "—"
        binding.agentLastCheckin.text = agent.time
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_AGENT_ID = "agentId"

        fun newInstance(agentId: Int): AgentInterfaceFragment =
            AgentInterfaceFragment().apply {
                arguments = Bundle().apply { putInt(ARG_AGENT_ID, agentId) }
            }
    }
}
