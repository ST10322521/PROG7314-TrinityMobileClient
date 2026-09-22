package Emeris.PROG7314.trinitymobileclient.ui.dashboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import Emeris.PROG7314.trinitymobileclient.model.Agent
import kotlin.math.min

/**
 * Lightweight Canvas graph for the Dashboard "Graph View" card (design-14).
 *
 * Draws a central TEAM SERVER node with EGRESS edges (thick orange) fanning out to the
 * connected agents, plus a P2P ring (thin dashed blue) linking the agent nodes together.
 * Pure [View] + [Canvas] — no Compose, no external dependencies.
 */
class AgentGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : View(context, attrs, defStyle) {

    private val density = resources.displayMetrics.density

    @Suppress("DEPRECATION")
    private val scaledDensity = resources.displayMetrics.scaledDensity

    /** Most recently supplied agents; only the first [MAX_AGENTS] are drawn. */
    private var agents: List<Agent> = emptyList()

    private val serverFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = COLOR_ORANGE
    }

    private val serverText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = COLOR_WHITE
        textAlign = Paint.Align.CENTER
        textSize = 11f * scaledDensity
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    private val agentFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = COLOR_CARD
    }

    private val agentText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = COLOR_WHITE
        textAlign = Paint.Align.CENTER
        textSize = 10f * scaledDensity
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    private val onlineDot = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = COLOR_GREEN
    }

    private val offlineDot = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = COLOR_RED
    }

    private val dotRing = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.5f * density
        color = COLOR_CARD
    }

    private val egressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f * density
        strokeCap = Paint.Cap.ROUND
        color = COLOR_ORANGE
    }

    private val p2pPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f * density
        strokeCap = Paint.Cap.ROUND
        color = COLOR_BLUE
        pathEffect = DashPathEffect(
            floatArrayOf(8f * density, 6f * density), 0f
        )
    }

    private val nodeRect = RectF()

    /** Replace the drawn agents and request a redraw. */
    fun setAgents(agents: List<Agent>) {
        this.agents = agents.take(MAX_AGENTS)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val list = agents
        if (list.isEmpty()) return

        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        val pad = 6f * density
        val serverHeight = 32f * density
        val serverWidth = min(w * 0.5f, 140f * density)

        // Central TEAM SERVER node, horizontally centred near the top.
        nodeRect.set(w / 2f - serverWidth / 2f, pad, w / 2f + serverWidth / 2f, pad + serverHeight)
        val serverRect = RectF(nodeRect)

        // Agent nodes spread evenly across the lower area.
        val count = list.size
        val slot = w / count
        val agentHeight = 28f * density
        val agentWidth = min(slot * 0.82f, 72f * density)
        val agentCenterY = h - agentHeight / 2f - pad

        val agentRects = ArrayList<RectF>(count)
        val agentCenters = ArrayList<Pair<Float, Float>>(count)
        list.forEachIndexed { index, _ ->
            val cx = slot * (index + 0.5f)
            val rect = RectF(
                cx - agentWidth / 2f,
                agentCenterY - agentHeight / 2f,
                cx + agentWidth / 2f,
                agentCenterY + agentHeight / 2f,
            )
            agentRects.add(rect)
            agentCenters.add(cx to agentCenterY)
        }

        // P2P ring: link agent nodes to each other (drawn first, behind everything).
        if (count >= 2) {
            for (i in 0 until count) {
                val from = agentCenters[i]
                val to = agentCenters[(i + 1) % count]
                canvas.drawLine(from.first, from.second, to.first, to.second, p2pPaint)
            }
        }

        // EGRESS edges: server -> each agent.
        val serverCenterX = serverRect.centerX()
        val serverBottom = serverRect.bottom
        agentCenters.forEach { (cx, _) ->
            canvas.drawLine(serverCenterX, serverBottom, cx, agentCenterY - agentHeight / 2f, egressPaint)
        }

        // Server node + label.
        canvas.drawRoundRect(serverRect, 8f * density, 8f * density, serverFill)
        drawCenteredText(canvas, "TEAM SERVER", serverRect.centerX(), serverRect.centerY(), serverText)

        // Agent nodes, status dots and labels.
        list.forEachIndexed { index, agent ->
            val rect = agentRects[index]
            canvas.drawRoundRect(rect, 7f * density, 7f * density, agentFill)

            val dotR = 4f * density
            val dotCx = rect.right - dotR - 3f * density
            val dotCy = rect.top + dotR + 3f * density
            val dotPaint = if (agent.online) onlineDot else offlineDot
            canvas.drawCircle(dotCx, dotCy, dotR + 1.5f * density, dotRing)
            canvas.drawCircle(dotCx, dotCy, dotR, dotPaint)

            drawCenteredText(
                canvas,
                shortName(agent.name),
                rect.centerX(),
                rect.centerY(),
                agentText,
            )
        }
    }

    /** "DOMAIN\\user" -> "user"; names without a backslash are unchanged. */
    private fun shortName(name: String): String {
        val short = name.substringAfterLast('\\').ifEmpty { name }
        return if (short.length > 12) short.take(11) + "\u2026" else short
    }

    private fun drawCenteredText(canvas: Canvas, text: String, cx: Float, cy: Float, paint: Paint) {
        val fm = paint.fontMetrics
        val baseline = cy - (fm.ascent + fm.descent) / 2f
        canvas.drawText(text, cx, baseline, paint)
    }

    private companion object {
        const val MAX_AGENTS = 4
        const val COLOR_ORANGE = 0xFFF57A1E.toInt()
        const val COLOR_BLUE = 0xFF3B82F6.toInt()
        const val COLOR_CARD = 0xFF1C1C1F.toInt()
        const val COLOR_WHITE = 0xFFFFFFFF.toInt()
        const val COLOR_GREEN = 0xFF35C759.toInt()
        const val COLOR_RED = 0xFFE5484D.toInt()
    }
}
