package Emeris.PROG7314.trinitymobileclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

// Dashboard screen. Agent rows open the Agent Interface screen.
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private data class Agent(
        val name: String, val meta: String, val time: String, val online: Boolean
    )

    // No agents yet.
    private val sampleAgents = emptyList<Agent>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = view.findViewById<LinearLayout>(R.id.agent_list)
        val empty = view.findViewById<View>(R.id.agent_empty)
        val inflater = LayoutInflater.from(requireContext())

        empty.visibility = if (sampleAgents.isEmpty()) View.VISIBLE else View.GONE

        sampleAgents.forEachIndexed { index, agent ->
            val row = inflater.inflate(R.layout.item_agent_row, list, false)
            row.findViewById<TextView>(R.id.agent_name).text = agent.name
            row.findViewById<TextView>(R.id.agent_meta).text = agent.meta
            row.findViewById<TextView>(R.id.agent_time).text = agent.time
            row.findViewById<View>(R.id.dot)
                .setBackgroundResource(if (agent.online) R.drawable.dot_green else R.drawable.dot_yellow)

            row.setOnClickListener { (activity as? HomeActivity)?.openAgentInterface() }
            list.addView(row)

            // divider between rows
            if (index < sampleAgents.lastIndex) {
                list.addView(inflater.inflate(R.layout.item_divider, list, false))
            }
        }
    }
}
