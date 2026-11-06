package Emeris.PROG7314.trinitymobileclient.ui

import Emeris.PROG7314.trinitymobileclient.R
import Emeris.PROG7314.trinitymobileclient.api.model.AgentDto
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class AgentGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private data class NodeBounds(val agent: AgentDto, val cx: Float, val cy: Float, val r: Float)

    private var agents: List<AgentDto> = emptyList()
    var onAgentTap: ((AgentDto) -> Unit)? = null

    private val dp = context.resources.displayMetrics.density
    private val serverRadius = dp * 38f
    private val agentRadius  = dp * 26f

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.divider)
        strokeWidth = dp * 1.5f
        style = Paint.Style.STROKE
    }
    private val serverFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.brand_orange)
        style = Paint.Style.FILL
    }
    private val serverRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.brand_orange_soft)
        strokeWidth = dp * 7f
        style = Paint.Style.STROKE
    }
    private val activeFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.status_green)
        style = Paint.Style.FILL
    }
    private val inactiveFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.status_yellow)
        style = Paint.Style.FILL
    }
    private val nodeRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.bg_surface)
        strokeWidth = dp * 2.5f
        style = Paint.Style.STROKE
    }
    private val serverLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = dp * 10f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }
    private val nodeInsideLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = dp * 9f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }
    private val nodeBelowLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.text_secondary)
        textSize = dp * 10f
        textAlign = Paint.Align.CENTER
    }
    private val emptyLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.text_muted)
        textSize = dp * 13f
        textAlign = Paint.Align.CENTER
    }

    private var drawnNodes: List<NodeBounds> = emptyList()

    fun setAgents(list: List<AgentDto>) {
        agents = list
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val cx = width / 2f
        val cy = height / 2f

        if (agents.isEmpty()) {
            canvas.drawCircle(cx, cy, serverRadius * 1.5f, serverRingPaint)
            canvas.drawCircle(cx, cy, serverRadius, serverFillPaint)
            canvas.drawText("SERVER", cx, cy + serverLabelPaint.textSize / 3f, serverLabelPaint)
            canvas.drawText("No agents connected", cx, cy + serverRadius + dp * 20f, emptyLabelPaint)
            drawnNodes = emptyList()
            return
        }

        val orbit = min(width, height) / 2f - agentRadius - dp * 24f
        val nodes = mutableListOf<NodeBounds>()

        // Lines first (drawn behind nodes)
        for (i in agents.indices) {
            val angle = (2.0 * Math.PI * i / agents.size - Math.PI / 2).toFloat()
            val nx = cx + orbit * cos(angle)
            val ny = cy + orbit * sin(angle)
            canvas.drawLine(cx, cy, nx, ny, linePaint)
            nodes.add(NodeBounds(agents[i], nx, ny, agentRadius))
        }

        // Server node
        canvas.drawCircle(cx, cy, serverRadius * 1.5f, serverRingPaint)
        canvas.drawCircle(cx, cy, serverRadius, serverFillPaint)
        canvas.drawText("SERVER", cx, cy + serverLabelPaint.textSize / 3f, serverLabelPaint)

        // Agent nodes
        for (node in nodes) {
            val fill = if (node.agent.isActive == true) activeFillPaint else inactiveFillPaint
            canvas.drawCircle(node.cx, node.cy, node.r, fill)
            canvas.drawCircle(node.cx, node.cy, node.r, nodeRingPaint)
            val label = (node.agent.hostname ?: node.agent.id ?: "?").take(8)
            canvas.drawText(label, node.cx, node.cy + nodeInsideLabelPaint.textSize / 3f, nodeInsideLabelPaint)
            canvas.drawText(label, node.cx, node.cy + node.r + dp * 14f, nodeBelowLabelPaint)
        }

        drawnNodes = nodes
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            for (node in drawnNodes) {
                val dist = sqrt((event.x - node.cx).pow(2) + (event.y - node.cy).pow(2))
                if (dist <= node.r + dp * 12f) {
                    onAgentTap?.invoke(node.agent)
                    return true
                }
            }
        }
        return true
    }
}
