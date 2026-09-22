package Emeris.PROG7314.trinitymobileclient.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.lifecycleScope
import Emeris.PROG7314.trinitymobileclient.HomeActivity
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentDashboardBinding
import Emeris.PROG7314.trinitymobileclient.databinding.ItemAgentRowBinding
import Emeris.PROG7314.trinitymobileclient.databinding.ItemDividerBinding
import Emeris.PROG7314.trinitymobileclient.model.Agent
import Emeris.PROG7314.trinitymobileclient.model.toUiAgent
import Emeris.PROG7314.trinitymobileclient.R
import Emeris.PROG7314.trinitymobileclient.viewmodel.AgentListViewModel
import Emeris.PROG7314.trinitymobileclient.viewmodel.AgentUiState
import kotlinx.coroutines.launch

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AgentListViewModel by viewModels {
        viewModelFactory {
            initializer { AgentListViewModel() }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return FragmentDashboardBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        viewModel.load()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is AgentUiState.Loading -> {
                        binding.agentList.removeAllViews()
                        showEmpty("Loading agents…")
                    }
                    is AgentUiState.Failed -> {
                        binding.agentList.removeAllViews()
                        showEmpty("Could not load agents — ${state.message}")
                    }
                    is AgentUiState.Loaded -> renderAgents(state.data.map { it.toUiAgent() })
                }
            }
        }
    }

    private fun showEmpty(message: String) {
        binding.agentEmptyText.text = message
        binding.agentEmpty.visibility = View.VISIBLE
    }

    private fun renderAgents(agents: List<Agent>) {
        binding.agentList.removeAllViews()
        if (agents.isEmpty()) {
            showEmpty("No agents")
            return
        }
        binding.agentEmpty.visibility = View.GONE
        agents.forEachIndexed { index, agent ->
            val rowBinding = ItemAgentRowBinding.inflate(layoutInflater, binding.agentList, false)
            rowBinding.agentName.text = agent.name
            rowBinding.agentMeta.text = agent.meta
            rowBinding.agentTime.text = agent.time
            rowBinding.dot.setBackgroundResource(
                if (agent.online) R.drawable.dot_green else R.drawable.dot_yellow
            )
            rowBinding.root.setOnClickListener {
                (activity as? HomeActivity)?.openAgentInterface(agent)
            }
            binding.agentList.addView(rowBinding.root)
            if (index < agents.lastIndex) {
                binding.agentList.addView(
                    ItemDividerBinding.inflate(layoutInflater, binding.agentList, false).root
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
