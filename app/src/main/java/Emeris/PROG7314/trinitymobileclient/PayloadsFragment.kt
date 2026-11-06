package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.api.ApiClient
import Emeris.PROG7314.trinitymobileclient.notifications.NotificationHelper
import Emeris.PROG7314.trinitymobileclient.api.model.CampaignDto
import Emeris.PROG7314.trinitymobileclient.api.model.ListenerDto
import Emeris.PROG7314.trinitymobileclient.api.model.OperatorDto
import Emeris.PROG7314.trinitymobileclient.api.model.PayloadCreationRequest
import Emeris.PROG7314.trinitymobileclient.api.model.PayloadDto
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentPayloadsBinding
import Emeris.PROG7314.trinitymobileclient.databinding.ItemDividerBinding
import Emeris.PROG7314.trinitymobileclient.databinding.ItemPayloadRowBinding
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PayloadsFragment : Fragment(R.layout.fragment_payloads) {

    private var _bindings: FragmentPayloadsBinding? = null
    // getter not eager init, eager init crashed the app
    private val bindings get() = _bindings!!

    // Form state
    private var listeners: List<ListenerDto>  = emptyList()
    private var campaigns: List<CampaignDto>  = emptyList()
    private var operators: List<OperatorDto>  = emptyList()
    private var selectedListener: ListenerDto? = null
    private var selectedCampaign: CampaignDto? = null

    // Arch: x32=0, x64=1, x86=2 (server enum order)
    private val archOptions      = listOf("x32" to 0, "x64" to 1, "x86" to 2)
    private val rotationOptions  = listOf("Round Robin" to 0, "Random" to 1)
    private var archIndex     = 1  // default x64
    private var rotationIndex = 0  // default ROUND_ROBIN

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _bindings = FragmentPayloadsBinding.bind(view)

        setupFormInteractions()
        loadData()
    }

    private fun setupFormInteractions() {
        bindings.tvListener.setOnClickListener { showListenerPicker() }
        bindings.tvCampaign.setOnClickListener { showCampaignPicker() }

        // Type is locked to WINDOWS_EXE — no interaction needed

        bindings.tvPayloadArch.setOnClickListener {
            archIndex = (archIndex + 1) % archOptions.size
            bindings.tvPayloadArch.text = archOptions[archIndex].first
        }

        bindings.tvRotationStrategy.setOnClickListener {
            rotationIndex = (rotationIndex + 1) % rotationOptions.size
            bindings.tvRotationStrategy.text = rotationOptions[rotationIndex].first
        }

        bindings.btnGenerate.setOnClickListener { onGenerateTapped() }
    }

    private fun loadData() {
        bindings.statePayloads.showLoading()
        bindings.payloadsContent.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            val listenersD  = async { runCatching { ApiClient.listeners.getListeners() } }
            val campaignsD  = async { runCatching { ApiClient.campaigns.getCampaigns() } }
            val operatorsD  = async { runCatching { ApiClient.operators.getOperators() } }
            val payloadsD   = async { runCatching { ApiClient.payloads.getPayloads() } }

            listeners = listenersD.await().getOrNull()?.body() ?: emptyList()
            campaigns = campaignsD.await().getOrNull()?.body() ?: emptyList()
            operators = operatorsD.await().getOrNull()?.body() ?: emptyList()
            val payloads = payloadsD.await().getOrNull()?.body() ?: emptyList()

            listenersD.await().onFailure { Log.e(TAG, "listeners error", it) }
            campaignsD.await().onFailure { Log.e(TAG, "campaigns error", it) }
            operatorsD.await().onFailure { Log.e(TAG, "operators error", it) }
            payloadsD.await().onFailure  { Log.e(TAG, "payloads error", it) }

            populatePayloadList(payloads)
        }
    }

    private fun showListenerPicker() {
        if (listeners.isEmpty()) {
            Toast.makeText(requireContext(), "No listeners available", Toast.LENGTH_SHORT).show()
            return
        }
        val names = listeners.map { it.name ?: "Listener ${it.id}" }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("Select Listener")
            .setItems(names) { _, i ->
                selectedListener = listeners[i]
                bindings.tvListener.text = names[i]
                bindings.tvListener.setTextColor(resources.getColor(R.color.text_primary, null))
            }
            .show()
    }

    private fun showCampaignPicker() {
        if (campaigns.isEmpty()) {
            Toast.makeText(requireContext(), "No campaigns available", Toast.LENGTH_SHORT).show()
            return
        }
        val names = campaigns.map { it.name.ifBlank { "Campaign ${it.id}" } }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("Select Campaign")
            .setItems(names) { _, i ->
                selectedCampaign = campaigns[i]
                bindings.tvCampaign.text = names[i]
                bindings.tvCampaign.setTextColor(resources.getColor(R.color.text_primary, null))
            }
            .show()
    }

    private fun onGenerateTapped() {
        val listenerId = selectedListener?.id
        val campaignId = selectedCampaign?.id
        val operatorId = operators.firstOrNull()?.id ?: 1

        if (listenerId == null) {
            Toast.makeText(requireContext(), "Select a listener", Toast.LENGTH_SHORT).show()
            return
        }
        if (campaignId == null) {
            Toast.makeText(requireContext(), "Select a campaign", Toast.LENGTH_SHORT).show()
            return
        }

        val name      = bindings.etPayloadName.text.toString().trim().ifEmpty { null }
        val sleep     = bindings.etSleep.text.toString().toIntOrNull() ?: 60
        val jitter    = bindings.etJitter.text.toString().toIntOrNull() ?: 0
        val arch      = archOptions[archIndex].second
        val rotation  = rotationOptions[rotationIndex].second
        val stageless = bindings.switchStageless.isChecked

        val request = PayloadCreationRequest(
            name             = name,
            operatorId       = operatorId,
            sleep            = sleep,
            jitter           = jitter,
            listenerId       = listenerId,
            campaignId       = campaignId,
            architecture     = arch,
            type             = 2,  // WINDOWS_EXE only
            rotationStrategy = rotation,
            isStageless      = stageless
        )

        bindings.btnGenerate.isEnabled = false
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.payloads.generatePayload(request)
                if (response.isSuccessful) {
                    NotificationHelper.showEvent(
                        context = requireContext(),
                        title   = "Payload Generated",
                        message = "New payload created successfully"
                    )
                    Toast.makeText(requireContext(), "Payload generated", Toast.LENGTH_SHORT).show()
                    loadData()
                } else {
                    Toast.makeText(requireContext(), "Failed: HTTP ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "generate error", e)
                Toast.makeText(requireContext(), "Unable to connect", Toast.LENGTH_SHORT).show()
            } finally {
                bindings.btnGenerate.isEnabled = true
            }
        }
    }

    private fun populatePayloadList(payloads: List<PayloadDto>) {
        if (payloads.isEmpty()) {
            bindings.payloadsContent.visibility = View.GONE
            bindings.statePayloads.showEmpty(
                icon    = R.drawable.ic_file,
                title   = R.string.state_payloads_empty_title,
                message = R.string.state_payloads_empty_message
            )
            return
        }

        bindings.statePayloads.showContent()
        bindings.payloadsContent.visibility = View.VISIBLE
        bindings.payloadsContent.removeAllViews()

        payloads.forEachIndexed { index, payload ->
            val row = ItemPayloadRowBinding.inflate(layoutInflater, bindings.payloadsContent, false)

            // PayloadType enum name from server
            val typeName = when (payload.type) {
                "POWERSHELL"      -> "PS1"
                "RAW"             -> "RAW"
                "WINDOWS_EXE"     -> "EXE"
                "WINDOWS_SERVICE" -> "SVC"
                "WINDOWS_DLL"     -> "DLL"
                else              -> payload.type ?: "???"
            }
            // Arch enum name from server
            val archName = payload.architecture ?: ""
            row.payloadTypeBadge.text = typeName
            row.payloadName.text = payload.name ?: "Payload #${payload.id}"

            val listenerName = listeners.find { it.id == payload.listenerId }?.name
                ?: payload.listenerId?.let { "Listener $it" } ?: "—"
            val campaignName = campaigns.find { it.id == payload.campaignId }?.name
                ?: payload.campaignId?.let { "Campaign $it" } ?: "—"
            row.payloadMeta.text = buildString {
                append(listenerName)
                append(" · ")
                append(campaignName)
                if (archName.isNotEmpty()) append(" · $archName")
            }
            row.payloadDate.text = payload.createdAt?.take(10) ?: ""

            bindings.payloadsContent.addView(row.root)

            if (index < payloads.lastIndex) {
                val div = ItemDividerBinding.inflate(layoutInflater, bindings.payloadsContent, false)
                bindings.payloadsContent.addView(div.root)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindings = null
    }

    private companion object {
        const val TAG = "PayloadsFragment"
    }
}
