package Emeris.PROG7314.trinitymobileclient.ui.listenermanager

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import Emeris.PROG7314.trinitymobileclient.R
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentListenerManagerBinding
import Emeris.PROG7314.trinitymobileclient.databinding.ItemListenerRowBinding
import Emeris.PROG7314.trinitymobileclient.viewmodel.ListenerListState
import Emeris.PROG7314.trinitymobileclient.viewmodel.ListenerListViewModel
import Emeris.PROG7314.trinitymobileclient.viewmodel.ListenerManagerViewModel
import Emeris.PROG7314.trinitymobileclient.viewmodel.ListenerRow
import kotlinx.coroutines.launch

/**
 * Listener Manager screen (F2.2), matched to Design.pdf design-16:
 * stats cards (TOTAL/ACTIVE/AGENTS), filter + New, listener rows
 * (status dot, name + protocol pill, bind address, agent count) and pagination.
 *
 * The list is populated from the generated typed client via [ListenerListViewModel]
 * (GET /api/v1/listeners -> List<HttpListenerDTO>); create actions go through the
 * generated [ListenerManagerViewModel].
 */
class ListenerManagerFragment : Fragment(R.layout.fragment_listener_manager) {

    private var _binding: FragmentListenerManagerBinding? = null
    private val binding get() = _binding!!

    private val listViewModel: ListenerListViewModel by viewModels {
        viewModelFactory { initializer { ListenerListViewModel() } }
    }

    private val createViewModel: ListenerManagerViewModel by viewModels {
        viewModelFactory { initializer { ListenerManagerViewModel() } }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = FragmentListenerManagerBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListenerManagerBinding.bind(view)

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.btnNew.setOnClickListener { showNewListenerDialog() }

        listViewModel.load()
        viewLifecycleOwner.lifecycleScope.launch {
            listViewModel.state.collect { render(it) }
        }
    }

    private fun render(state: ListenerListState) {
        binding.statTotal.text = state.total.toString()
        binding.statActive.text = state.activeCount.toString()
        binding.statAgents.text = state.rows.sumOf { it.agents }.toString()

        binding.listenerList.removeAllViews()
        when {
            state.error != null -> {
                binding.listenerEmpty.visibility = View.VISIBLE
                binding.listenerEmpty.text = state.error
            }
            state.rows.isEmpty() -> {
                binding.listenerEmpty.visibility = View.VISIBLE
                binding.listenerEmpty.text =
                    if (state.loading) "Loading listeners…" else "No listeners yet"
            }
            else -> {
                binding.listenerEmpty.visibility = View.GONE
                state.rows.forEach { addRow(it) }
            }
        }
    }

    private fun addRow(row: ListenerRow) {
        val rb = ItemListenerRowBinding.inflate(layoutInflater, binding.listenerList, false)
        rb.rowName.text = row.name
        rb.rowPill.text = row.protocol
        rb.rowBind.text = row.bind
        rb.rowAgents.text = row.agents.toString()
        rb.rowDot.setBackgroundResource(if (row.active) R.drawable.dot_green else R.drawable.dot_red)
        binding.listenerList.addView(rb.root)
    }

    private fun showNewListenerDialog() {
        val ctx = requireContext()
        val name = EditText(ctx).apply { hint = "Name (e.g. http-primary)" }
        val host = EditText(ctx).apply { hint = "Host (e.g. 10.0.0.5)" }
        val port = EditText(ctx).apply {
            hint = "Bind port (e.g. 8080)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        val box = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
            addView(name); addView(host); addView(port)
        }
        AlertDialog.Builder(ctx)
            .setTitle("New HTTP listener")
            .setView(box)
            .setPositiveButton("Create") { _, _ ->
                val p = port.text.toString().trim().toIntOrNull() ?: 80
                createViewModel.createHttp(
                    name.text.toString().trim(),
                    host.text.toString().trim(),
                    p
                )
                listViewModel.load()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
