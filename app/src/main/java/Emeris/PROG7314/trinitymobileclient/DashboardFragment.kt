package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.api.ApiClient
import Emeris.PROG7314.trinitymobileclient.api.model.AgentDto
import Emeris.PROG7314.trinitymobileclient.api.model.CampaignDto
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentDashboardBinding
import Emeris.PROG7314.trinitymobileclient.databinding.ItemAgentRowBinding
import Emeris.PROG7314.trinitymobileclient.databinding.ItemDividerBinding
import Emeris.PROG7314.trinitymobileclient.model.EventLog
import Emeris.PROG7314.trinitymobileclient.notifications.NotificationHelper
import Emeris.PROG7314.trinitymobileclient.signalr.SignalRManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private companion object {
        const val HUB_URL   = "https://urchin-app-uisgp.ondigitalocean.app/hub/events"
        const val CAMPAIGN_ID = "1"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        binding.graphView.onAgentTap = { agent ->
            agent.id?.let { (activity as? HomeActivity)?.openAgentInterface(it) }
        }

        loadDashboard()
        connectHub()
    }

    private fun connectHub() {
        val signalR = SignalRManager.getInstance()
        signalR.connect(
            hubUrl        = HUB_URL,
            tokenProvider = { ApiClient.token ?: "" },
            onConnected   = {
                signalR.on("EventLogNotification", EventLog::class.java) { event ->
                    val ctx = context ?: return@on
                    NotificationHelper.showEvent(
                        context = ctx,
                        title   = event.type.ifBlank { "Event" },
                        message = event.message
                    )
                }
                signalR.subscribeToEventLog(
                    campaignId   = CAMPAIGN_ID,
                    onSubscribed = { Log.d("Dashboard", "Subscribed to event log campaign $CAMPAIGN_ID") },
                    onError      = { e -> Log.e("Dashboard", "Event log subscribe failed", e) }
                )
            },
            onError = { e -> Log.e("Dashboard", "Hub connect failed", e) }
        )
    }

    private fun loadDashboard() {
        viewLifecycleOwner.lifecycleScope.launch {
            val agentsDeferred    = async { runCatching { ApiClient.agents.getAgents() } }
            val campaignsDeferred = async { runCatching { ApiClient.campaigns.getCampaigns() } }
            val statusDeferred    = async { runCatching { ApiClient.server.getStatus() } }

            val agentsResult    = agentsDeferred.await()
            val campaignsResult = campaignsDeferred.await()
            val statusResult    = statusDeferred.await()

            val agents    = agentsResult.getOrNull()?.body() ?: emptyList()
            val campaigns = campaignsResult.getOrNull()?.body() ?: emptyList()

            agentsResult.onFailure    { Log.e("Dashboard", "agents error", it) }
            campaignsResult.onFailure { Log.e("Dashboard", "campaigns error", it) }
            statusResult.onFailure    { Log.e("Dashboard", "server status error", it) }

            updateStats(agents, campaigns)
            updateServerStatus(statusResult.isSuccess && statusResult.getOrNull()?.isSuccessful == true)
            updateGraph(agents)
            updateCampaignChips(campaigns)
            updateAgentList(agents)
        }
    }

    private fun updateStats(agents: List<AgentDto>, campaigns: List<CampaignDto>) {
        binding.tvStatTotal.text     = agents.size.toString()
        binding.tvStatActive.text    = agents.count { it.isActive == true }.toString()
        binding.tvStatCampaigns.text = campaigns.size.toString()
    }

    private fun updateServerStatus(online: Boolean) {
        binding.tvServerStatus.text = if (online) "● Server Online" else "● Server Offline"
        binding.tvServerStatus.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (online) R.color.status_green else R.color.status_yellow
            )
        )
    }

    private fun updateGraph(agents: List<AgentDto>) {
        binding.graphView.setAgents(agents)
    }

    private fun updateCampaignChips(campaigns: List<CampaignDto>) {
        binding.campaignChips.removeAllViews()
        if (campaigns.isEmpty()) {
            binding.campaignEmpty.visibility = View.VISIBLE
            return
        }
        binding.campaignEmpty.visibility = View.GONE
        val dp = resources.displayMetrics.density
        campaigns.forEach { campaign ->
            val chip = TextView(requireContext()).apply {
                text = campaign.name.ifBlank { "Campaign ${campaign.id}" }
                textSize = 13f
                setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
                setBackgroundResource(R.drawable.bg_input)
                setPadding((12 * dp).toInt(), (6 * dp).toInt(), (12 * dp).toInt(), (6 * dp).toInt())
            }
            val params = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { marginEnd = (8 * dp).toInt() }
            binding.campaignChips.addView(chip, params)
        }
    }

    private fun updateAgentList(agents: List<AgentDto>) {
        val empty = binding.agentEmpty
        binding.agentList.removeAllViews()
        binding.agentList.addView(empty)
        empty.visibility = if (agents.isEmpty()) View.VISIBLE else View.GONE

        agents.forEachIndexed { index, agent ->
            val rowBinding = ItemAgentRowBinding.inflate(layoutInflater, binding.agentList, false)

            rowBinding.agentName.text = agent.hostname ?: agent.id ?: "Unknown"
            rowBinding.agentMeta.text = buildString {
                agent.username?.let { append(it) }
                agent.os?.let { if (isNotEmpty()) append(" · "); append(it) }
                agent.internalIp?.let { if (isNotEmpty()) append(" · "); append(it) }
            }
            rowBinding.agentTime.text = agent.lastSeen ?: ""
            rowBinding.dot.setBackgroundResource(
                if (agent.isActive == true) R.drawable.dot_green else R.drawable.dot_yellow
            )
            rowBinding.root.setOnClickListener {
                agent.id?.let { id -> (activity as? HomeActivity)?.openAgentInterface(id) }
            }

            binding.agentList.addView(rowBinding.root)

            if (index < agents.lastIndex) {
                val dividerBinding = ItemDividerBinding.inflate(layoutInflater, binding.agentList, false)
                binding.agentList.addView(dividerBinding.root)
            }
        }
    }

    override fun onDestroyView() {
        val signalR = SignalRManager.getInstance()
        if (signalR.isConnected) {
            signalR.unsubscribeFromEventLog(CAMPAIGN_ID)
            signalR.disconnect()
        }
        super.onDestroyView()
        _binding = null
    }
}
