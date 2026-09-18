package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.databinding.FragmentDashboardBinding
import Emeris.PROG7314.trinitymobileclient.databinding.ItemAgentRowBinding
import Emeris.PROG7314.trinitymobileclient.databinding.ItemDividerBinding
import Emeris.PROG7314.trinitymobileclient.model.Agent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.forEachIndexed
import androidx.fragment.app.Fragment

// Dashboard screen. Agent rows open the Agent Interface screen.
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    // View Binding setup
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // No agents yet.
    private val sampleAgents = emptyList<Agent>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentDashboardBinding.bind(view)

        binding.agentEmpty.visibility = if (sampleAgents.isEmpty()) View.VISIBLE else View.GONE

        sampleAgents.forEachIndexed { index, agent ->

            val rowBinding = ItemAgentRowBinding.inflate(
                layoutInflater,
                binding.agentList,
                false
            )

            rowBinding.agentName.text = agent.name
            rowBinding.agentMeta.text = agent.meta
            rowBinding.agentTime.text = agent.time

            rowBinding.dot.setBackgroundResource((if (agent.online) R.drawable.dot_green else R.drawable.dot_yellow))

            rowBinding.root.setOnClickListener {
                (activity as? HomeActivity)?.openAgentInterface()
            }

            // divider between rows
            if (index < sampleAgents.lastIndex) {
                val dividerBinding = ItemDividerBinding.inflate(
                    layoutInflater,
                    binding.agentList,
                    false
                )

                binding.agentList.addView(dividerBinding.root)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
