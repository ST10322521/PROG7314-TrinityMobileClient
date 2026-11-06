package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.api.ApiClient
import Emeris.PROG7314.trinitymobileclient.api.model.CreateHttpListenerRequest
import Emeris.PROG7314.trinitymobileclient.api.model.CreateHttpsListenerRequest
import Emeris.PROG7314.trinitymobileclient.api.model.CreateSmbListenerRequest
import Emeris.PROG7314.trinitymobileclient.api.model.CreateTcpListenerRequest
import Emeris.PROG7314.trinitymobileclient.api.model.HttpConfig
import Emeris.PROG7314.trinitymobileclient.api.model.HttpsConfig
import Emeris.PROG7314.trinitymobileclient.api.model.ListenerDto
import Emeris.PROG7314.trinitymobileclient.api.model.SmbConfig
import Emeris.PROG7314.trinitymobileclient.api.model.TcpConfig
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentListenersBinding
import Emeris.PROG7314.trinitymobileclient.databinding.ItemDividerBinding
import Emeris.PROG7314.trinitymobileclient.databinding.ItemListenerRowBinding
import Emeris.PROG7314.trinitymobileclient.notifications.NotificationHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ListenersFragment : Fragment(R.layout.fragment_listeners) {

    private var _bindings: FragmentListenersBinding? = null
    // getter not eager init, eager init crashed the app
    private val bindings get() = _bindings!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _bindings = FragmentListenersBinding.bind(view)

        bindings.btnNewListener.setOnClickListener { showProtocolPicker() }
        loadData()
    }

    private fun loadData() {
        bindings.listenersContent.visibility = View.GONE
        bindings.stateListeners.showLoading()

        viewLifecycleOwner.lifecycleScope.launch {
            val listeners = runCatching { ApiClient.listeners.getListeners() }
                .onFailure { Log.e(TAG, "listeners error", it) }
                .getOrNull()?.body() ?: emptyList()

            updateStats(listeners)
            populateList(listeners)
        }
    }

    private fun updateStats(listeners: List<ListenerDto>) {
        var http = 0; var https = 0; var tcp = 0; var smb = 0; var tor = 0
        for (l in listeners) {
            when (l.protocol) {
                "HTTP"  -> http++
                "HTTPS" -> https++
                "TCP"   -> tcp++
                "SMB"   -> smb++
                "TOR"   -> tor++
            }
        }
        bindings.tvStatTotal.text = listeners.size.toString()
        bindings.tvStatHttp.text  = http.toString()
        bindings.tvStatHttps.text = https.toString()
        bindings.tvStatTcp.text   = tcp.toString()
        bindings.tvStatSmb.text   = smb.toString()
    }

    private fun populateList(listeners: List<ListenerDto>) {
        if (listeners.isEmpty()) {
            bindings.listenersContent.visibility = View.GONE
            bindings.stateListeners.showEmpty(
                icon    = R.drawable.ic_listeners,
                title   = R.string.state_listeners_empty_title,
                message = R.string.state_listeners_empty_message
            )
            return
        }

        bindings.stateListeners.showContent()
        bindings.listenersContent.visibility = View.VISIBLE
        bindings.listenersContent.removeAllViews()

        listeners.forEachIndexed { index, listener ->
            val row = ItemListenerRowBinding.inflate(layoutInflater, bindings.listenersContent, false)

            val active = listener.isActive == true
            row.listenerDot.setBackgroundResource(
                if (active) R.drawable.dot_green else R.drawable.dot_yellow
            )
            row.listenerName.text = listener.name ?: "Listener #${listener.id}"
            row.listenerMeta.text = listener.protocol ?: "unknown"

            val (badgeLabel, badgeColor) = when (listener.protocol) {
                "HTTP"  -> "HTTP"  to R.color.status_blue
                "HTTPS" -> "HTTPS" to R.color.status_green
                "TCP"   -> "TCP"   to R.color.status_purple
                "SMB"   -> "SMB"   to R.color.status_yellow
                "TOR"   -> "TOR"   to R.color.status_red
                else    -> (listener.protocol ?: "?") to R.color.text_muted
            }
            row.listenerTypeBadge.text = badgeLabel
            row.listenerTypeBadge.setTextColor(ContextCompat.getColor(requireContext(), badgeColor))

            row.root.setOnClickListener { showListenerActions(listener) }

            bindings.listenersContent.addView(row.root)

            if (index < listeners.lastIndex) {
                val div = ItemDividerBinding.inflate(layoutInflater, bindings.listenersContent, false)
                bindings.listenersContent.addView(div.root)
            }
        }
    }

    private fun showListenerActions(listener: ListenerDto) {
        val isHttp = listener.protocol?.uppercase() == "HTTP"
        val options = buildList {
            if (isHttp) add("Restart")
            add("Delete")
        }.toTypedArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(listener.name ?: "Listener #${listener.id}")
            .setItems(options) { _, i ->
                val choice = options[i]
                when {
                    choice == "Restart" -> restartListener(listener)
                    choice == "Delete"  -> confirmDelete(listener)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun restartListener(listener: ListenerDto) {
        val id = listener.id ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.listeners.restartHttp(id)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Listener restarted", Toast.LENGTH_SHORT).show()
                    loadData()
                } else {
                    Toast.makeText(requireContext(), "Failed: HTTP ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "restart error", e)
                Toast.makeText(requireContext(), "Unable to connect", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmDelete(listener: ListenerDto) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete listener?")
            .setMessage("\"${listener.name ?: "Listener #${listener.id}"}\" will be permanently removed.")
            .setPositiveButton("Delete") { _, _ -> deleteListener(listener) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteListener(listener: ListenerDto) {
        val id = listener.id ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.listeners.delete(id)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Listener deleted", Toast.LENGTH_SHORT).show()
                    loadData()
                } else {
                    Toast.makeText(requireContext(), "Failed: HTTP ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "delete error", e)
                Toast.makeText(requireContext(), "Unable to connect", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ── Create listener flow ───────────────────────────────────────────────

    private fun showProtocolPicker() {
        val protocols = arrayOf("HTTP", "HTTPS", "TCP", "SMB")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Protocol")
            .setItems(protocols) { _, i ->
                when (i) {
                    0 -> showCreateHttp()
                    1 -> showCreateHttps()
                    2 -> showCreateTcp()
                    3 -> showCreateSmb()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCreateHttp() {
        val (view, fields) = buildForm("Name", "Bind Port", "C2 Port", "Hosts (comma-separated)", "Max Retry Strategy")
        val (rotationView, rotationPicker) = buildRotationPicker(view)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("New HTTP Listener")
            .setView(rotationView)
            .setPositiveButton("Create") { _, _ ->
                val name     = fields[0].text.toString().trim().ifEmpty { null }
                val bindPort = fields[1].text.toString().toIntOrNull() ?: 0
                val c2Port   = fields[2].text.toString().toIntOrNull() ?: 0
                val hosts    = fields[3].text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val maxRetry = fields[4].text.toString().trim()
                if (hosts.isEmpty()) {
                    Toast.makeText(requireContext(), "At least one host is required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val request = CreateHttpListenerRequest(
                    name   = name,
                    config = HttpConfig(
                        bindPort         = bindPort,
                        c2Port           = c2Port,
                        userAgent        = null,
                        header           = null,
                        hosts            = hosts,
                        rotationStrategy = rotationPicker.tag as String,
                        maxRetryStrategy = maxRetry
                    )
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    createListener { ApiClient.listeners.createHttp(request) }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCreateHttps() {
        val (view, fields) = buildForm("Name", "C2 Port", "Hosts (comma-separated)", "Max Retry Strategy")
        val (rotationView, rotationPicker) = buildRotationPicker(view)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("New HTTPS Listener")
            .setView(rotationView)
            .setPositiveButton("Create") { _, _ ->
                val name     = fields[0].text.toString().trim().ifEmpty { null }
                val c2Port   = fields[1].text.toString().toIntOrNull() ?: 0
                val hosts    = fields[2].text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val maxRetry = fields[3].text.toString().trim()
                if (hosts.isEmpty()) {
                    Toast.makeText(requireContext(), "At least one host is required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val request = CreateHttpsListenerRequest(
                    name   = name,
                    config = HttpsConfig(
                        c2Port           = c2Port,
                        userAgent        = null,
                        header           = null,
                        hosts            = hosts,
                        rotationStrategy = rotationPicker.tag as String,
                        maxRetryStrategy = maxRetry
                    )
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    createListener { ApiClient.listeners.createHttps(request) }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCreateTcp() {
        val (view, fields) = buildForm("Name", "Port")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("New TCP Listener")
            .setView(view)
            .setPositiveButton("Create") { _, _ ->
                val name    = fields[0].text.toString().trim().ifEmpty { null }
                val port    = fields[1].text.toString().toIntOrNull() ?: 0
                val request = CreateTcpListenerRequest(name = name, config = TcpConfig(port = port))
                viewLifecycleOwner.lifecycleScope.launch {
                    createListener { ApiClient.listeners.createTcp(request) }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCreateSmb() {
        val (view, fields) = buildForm("Name", "Pipe Name")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("New SMB Listener")
            .setView(view)
            .setPositiveButton("Create") { _, _ ->
                val name    = fields[0].text.toString().trim().ifEmpty { null }
                val pipe    = fields[1].text.toString().trim().ifEmpty { null }
                val request = CreateSmbListenerRequest(name = name, config = SmbConfig(pipe = pipe))
                viewLifecycleOwner.lifecycleScope.launch {
                    createListener { ApiClient.listeners.createSmb(request) }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private suspend fun createListener(call: suspend () -> retrofit2.Response<okhttp3.ResponseBody>) {
        try {
            val response = call()
            if (response.isSuccessful) {
                NotificationHelper.showEvent(
                    context = requireContext(),
                    title   = "Listener Created",
                    message = "New listener is now active"
                )
                Toast.makeText(requireContext(), "Listener created", Toast.LENGTH_SHORT).show()
                loadData()
            } else {
                Toast.makeText(requireContext(), "Failed: HTTP ${response.code()}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "create error", e)
            Toast.makeText(requireContext(), "Unable to connect", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildForm(vararg labels: String): Pair<LinearLayout, List<EditText>> {
        val dp           = resources.displayMetrics.density
        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.text_primary)
        val colorMuted   = ContextCompat.getColor(requireContext(), R.color.text_muted)
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((20 * dp).toInt(), (12 * dp).toInt(), (20 * dp).toInt(), (12 * dp).toInt())
        }
        val fields = labels.mapIndexed { i, label ->
            android.widget.TextView(requireContext()).apply {
                text = label.uppercase()
                textSize = 11f
                setTextColor(colorMuted)
                letterSpacing = 0.08f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = if (i == 0) 0 else (14 * dp).toInt() }
            }.also { container.addView(it) }

            EditText(requireContext()).apply {
                hint = label
                setHintTextColor(colorMuted)
                setTextColor(colorPrimary)
                textSize = 14f
                setBackgroundResource(R.drawable.bg_input)
                setPadding((14 * dp).toInt(), 0, (14 * dp).toInt(), 0)
                gravity = android.view.Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (46 * dp).toInt()
                ).apply { topMargin = (6 * dp).toInt() }
                if (label.contains("Port", ignoreCase = true)) {
                    inputType = android.text.InputType.TYPE_CLASS_NUMBER
                }
            }.also { container.addView(it) }
        }
        return container to fields
    }

    private fun buildRotationPicker(existingContainer: LinearLayout): Pair<LinearLayout, android.widget.TextView> {
        val dp           = resources.displayMetrics.density
        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.text_primary)
        val colorMuted   = ContextCompat.getColor(requireContext(), R.color.text_muted)

        existingContainer.addView(android.widget.TextView(requireContext()).apply {
            text = "ROTATION STRATEGY"
            textSize = 11f
            setTextColor(colorMuted)
            letterSpacing = 0.08f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = (14 * dp).toInt() }
        })

        val picker = android.widget.TextView(requireContext()).apply {
            text = "Round Robin"
            tag  = "ROUND_ROBIN"
            textSize = 14f
            setTextColor(colorPrimary)
            setPadding((14 * dp).toInt(), 0, (14 * dp).toInt(), 0)
            setBackgroundResource(R.drawable.bg_input)
            gravity = android.view.Gravity.CENTER_VERTICAL
            compoundDrawablePadding = (8 * dp).toInt()
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_down, 0)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (46 * dp).toInt()
            ).apply { topMargin = (6 * dp).toInt() }
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val next      = if (tag == "ROUND_ROBIN") "RANDOM" else "ROUND_ROBIN"
                val nextLabel = if (next == "ROUND_ROBIN") "Round Robin" else "Random"
                tag  = next
                text = nextLabel
            }
        }.also { existingContainer.addView(it) }

        return existingContainer to picker
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindings = null
    }

    private companion object {
        const val TAG = "ListenersFragment"
    }
}
