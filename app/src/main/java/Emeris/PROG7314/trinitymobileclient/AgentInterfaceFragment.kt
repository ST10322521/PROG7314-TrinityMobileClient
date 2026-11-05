package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.data.CommandRepository
import Emeris.PROG7314.trinitymobileclient.data.command.CommandParserRegistry
import Emeris.PROG7314.trinitymobileclient.data.command.parser.ParseResult
import Emeris.PROG7314.trinitymobileclient.data.sync.CommandSyncScheduler
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentAgentInterfaceBinding
import Emeris.PROG7314.trinitymobileclient.signalr.SignalRManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AgentInterfaceFragment : Fragment(R.layout.fragment_agent_interface) {

    private var _binding: FragmentAgentInterfaceBinding? = null
    private val binding get() = _binding!!

    private lateinit var commandRepository: CommandRepository

    /** Agent ID passed in via [newInstance] — used for SignalR and command queuing. */
    private var agentId: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAgentInterfaceBinding.bind(view)

        agentId = arguments?.getString(ARG_AGENT_ID) ?: ""
        commandRepository = CommandRepository(requireContext())

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnClearConsole.setOnClickListener {
            // TODO: clear the console output view
        }

        wireSendButton()
        subscribeToAgentHub()
    }

    // ── Command input ─────────────────────────────────────────────────────────

    private fun wireSendButton() {
        binding.btnSendCommand.setOnClickListener {
            val rawInput = binding.inputCommand.text?.toString()?.trim() ?: return@setOnClickListener
            if (rawInput.isBlank()) return@setOnClickListener

            binding.inputCommand.text?.clear()

            when (val result = CommandParserRegistry.parse(rawInput)) {

                is ParseResult.Success -> {
                    appendToConsole("> $rawInput")
                    lifecycleScope.launch {
                        commandRepository.queueCommand(
                            agentId     = agentId,
                            commandType = result.commandType,
                            dto         = result.dto
                        )
                        // Schedule sync — WorkManager will fire once internet is available
                        CommandSyncScheduler.schedule(requireContext())
                        Log.d(TAG, "Command queued and sync scheduled: ${result.commandType}")
                    }
                }

                is ParseResult.UnknownCommand ->
                    appendToConsole("[!] Unknown command '${result.keyword}'. Available: ${CommandParserRegistry.registeredKeywords()}")

                is ParseResult.ParseError ->
                    appendToConsole("[!] ${result.message}")
            }
        }
    }

    // ── Console output ────────────────────────────────────────────────────────

    private fun appendToConsole(text: String) {
        // TODO: replace with your actual console TextView append logic
        Log.d(TAG, "Console: $text")
    }

    // ── SignalR ───────────────────────────────────────────────────────────────

    private fun subscribeToAgentHub() {
        if (agentId.isBlank()) {
            Log.w(TAG, "No agentId — skipping hub subscription")
            return
        }

        SignalRManager.getInstance().subscribeToAgent(
            agentId      = agentId,
            onSubscribed = { listenForTaskNotifications() },
            onError      = { e -> Log.e(TAG, "Hub subscribe failed for agent '$agentId'", e) }
        )
    }

    private fun listenForTaskNotifications() {
        SignalRManager.getInstance().on(
            "TaskNotification",
            Emeris.PROG7314.trinitymobileclient.model.TaskNotification::class.java
        ) { notification ->
            requireActivity().runOnUiThread {
                appendToConsole("[${notification.status}] ${notification.output}")
            }
        }
    }

    override fun onDestroyView() {
        if (agentId.isNotBlank()) {
            SignalRManager.getInstance().unsubscribeFromAgent(agentId)
        }
        super.onDestroyView()
        _binding = null
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    companion object {
        private const val TAG           = "AgentInterfaceFragment"
        private const val ARG_AGENT_ID  = "agentId"

        fun newInstance(agentId: String) = AgentInterfaceFragment().apply {
            arguments = Bundle().apply { putString(ARG_AGENT_ID, agentId) }
        }
    }
}
