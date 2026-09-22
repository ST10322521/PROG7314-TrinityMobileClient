package Emeris.PROG7314.trinitymobileclient.ui.agentmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import Emeris.PROG7314.trinitymobileclient.R
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentAgentInterfaceBinding
import Emeris.PROG7314.trinitymobileclient.model.Agent
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import Emeris.PROG7314.trinitymobileclient.model.toUiAgent
import Emeris.PROG7314.trinitymobileclient.viewmodel.AgentDetailViewModel
import Emeris.PROG7314.trinitymobileclient.viewmodel.AgentUiState
import kotlinx.coroutines.launch

class AgentInterfaceFragment : Fragment(R.layout.fragment_agent_interface) {

    private var _binding: FragmentAgentInterfaceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AgentDetailViewModel by viewModels {
        viewModelFactory {
            initializer { AgentDetailViewModel() }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return FragmentAgentInterfaceBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAgentInterfaceBinding.bind(view)
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val agentId = arguments?.getInt(ARG_AGENT_ID) ?: return
        viewModel.load(agentId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is AgentUiState.Loading -> binding.agentTitle.text = "Loading…"
                    is AgentUiState.Failed -> binding.agentTitle.text = "Error: ${state.message}"
                    is AgentUiState.Loaded -> renderAgent(state.data.toUiAgent())
                }
            }
        }
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
