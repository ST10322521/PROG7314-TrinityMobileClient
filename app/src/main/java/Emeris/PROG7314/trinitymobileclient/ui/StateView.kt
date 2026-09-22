package Emeris.PROG7314.trinitymobileclient.ui

import Emeris.PROG7314.trinitymobileclient.R
import Emeris.PROG7314.trinitymobileclient.databinding.ViewStateBinding
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Reusable UI-state container for the four states the prototype brief requires:
 * loading, empty, error and success.
 *
 * Screens drop one of these into their layout where data will eventually be
 * rendered, then call the matching `show*` function. [showContent] hides the
 * component entirely so the screen's real content can take over — that is the
 * call the API/database work should make once data has actually arrived.
 *
 * Kept deliberately free of any business logic: this is UI only, so the
 * authentication, API and custom-feature work can drive it from the outside
 * without depending on anything in here.
 *
 * Reference: the compound-view pattern used below (inflate a `<merge>` layout
 * into a custom ViewGroup) follows the Android documentation for custom view
 * components — https://developer.android.com/develop/ui/views/layout/custom-views/custom-view-components
 */
class StateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewStateBinding

    init {
        orientation = VERTICAL
        gravity = android.view.Gravity.CENTER_HORIZONTAL
        //Card styling matches @style/TrinityCard so state blocks sit
        //consistently alongside the rest of the screen's cards.
        setBackgroundResource(R.drawable.bg_card)
        val vertical = resources.getDimensionPixelSize(R.dimen.state_vertical_padding)
        val horizontal = resources.getDimensionPixelSize(R.dimen.state_horizontal_padding)
        setPadding(horizontal, vertical, horizontal, vertical)

        binding = ViewStateBinding.inflate(LayoutInflater.from(context), this)
    }

    /** Spinner + caption. Use while a request is in flight. */
    fun showLoading(@StringRes title: Int = R.string.state_loading) {
        Log.d(TAG, "showLoading")
        reset()
        binding.stateProgress.visibility = View.VISIBLE
        binding.stateTitle.setText(title)
        visibility = View.VISIBLE
    }

    /** Icon + title + message. Use when a request succeeded but returned nothing. */
    fun showEmpty(@DrawableRes icon: Int, @StringRes title: Int, @StringRes message: Int) {
        Log.d(TAG, "showEmpty")
        reset()
        showIcon(icon, R.color.text_muted)
        binding.stateTitle.setText(title)
        binding.stateMessage.setText(message)
        binding.stateMessage.visibility = View.VISIBLE
        visibility = View.VISIBLE
    }

    /**
     * Error icon + message, with an optional retry button.
     *
     * @param onRetry when supplied, a Retry button is shown and this is invoked on tap.
     */
    fun showError(
        @StringRes title: Int = R.string.state_error_title,
        @StringRes message: Int = R.string.state_error_message,
        onRetry: (() -> Unit)? = null
    ) {
        Log.d(TAG, "showError")
        reset()
        showIcon(R.drawable.ic_error_state, R.color.status_red)
        binding.stateTitle.setText(title)
        binding.stateMessage.setText(message)
        binding.stateMessage.visibility = View.VISIBLE

        if (onRetry != null) {
            binding.stateRetry.visibility = View.VISIBLE
            binding.stateRetry.setOnClickListener {
                Log.d(TAG, "retry tapped")
                onRetry()
            }
        }
        visibility = View.VISIBLE
    }

    /** Tick + confirmation copy. Use after a write/generate action completes. */
    fun showSuccess(
        @StringRes title: Int = R.string.state_success_title,
        @StringRes message: Int = R.string.state_success_message
    ) {
        Log.d(TAG, "showSuccess")
        reset()
        showIcon(R.drawable.ic_check_circle, R.color.status_green)
        binding.stateTitle.setText(title)
        binding.stateMessage.setText(message)
        binding.stateMessage.visibility = View.VISIBLE
        visibility = View.VISIBLE
    }

    /** Hides the whole component so the screen's real content is visible. */
    fun showContent() {
        Log.d(TAG, "showContent")
        reset()
        visibility = View.GONE
    }

    /** Tints and reveals the state icon. */
    private fun showIcon(@DrawableRes icon: Int, colourRes: Int) {
        binding.stateIcon.setImageResource(icon)
        binding.stateIcon.setColorFilter(
            androidx.core.content.ContextCompat.getColor(context, colourRes)
        )
        binding.stateIcon.visibility = View.VISIBLE
    }

    /** Clears every state-specific row so the next show* call starts clean. */
    private fun reset() {
        binding.stateProgress.visibility = View.GONE
        binding.stateIcon.visibility = View.GONE
        binding.stateMessage.visibility = View.GONE
        binding.stateRetry.visibility = View.GONE
        binding.stateRetry.setOnClickListener(null)
    }

    private companion object {
        const val TAG = "StateView"
    }
}
