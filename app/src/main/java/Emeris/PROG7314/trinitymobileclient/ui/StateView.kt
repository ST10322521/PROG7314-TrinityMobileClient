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

// one card for the loading / empty / error / success states, used by Listeners, Logs and Payloads
// UI only, the screens decide which state to show
// https://developer.android.com/develop/ui/views/layout/custom-views/custom-view-components
class StateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewStateBinding

    init {
        orientation = VERTICAL
        gravity = android.view.Gravity.CENTER_HORIZONTAL
        setBackgroundResource(R.drawable.bg_card)
        val vertical = resources.getDimensionPixelSize(R.dimen.state_vertical_padding)
        val horizontal = resources.getDimensionPixelSize(R.dimen.state_horizontal_padding)
        setPadding(horizontal, vertical, horizontal, vertical)

        binding = ViewStateBinding.inflate(LayoutInflater.from(context), this)
    }

    fun showLoading(@StringRes title: Int = R.string.state_loading) {
        Log.d(TAG, "showLoading")
        reset()
        binding.stateProgress.visibility = View.VISIBLE
        binding.stateTitle.setText(title)
        visibility = View.VISIBLE
    }

    fun showEmpty(@DrawableRes icon: Int, @StringRes title: Int, @StringRes message: Int) {
        Log.d(TAG, "showEmpty")
        reset()
        showIcon(icon, R.color.text_muted)
        binding.stateTitle.setText(title)
        binding.stateMessage.setText(message)
        binding.stateMessage.visibility = View.VISIBLE
        visibility = View.VISIBLE
    }

    // retry button only shows when you pass onRetry
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

    // hides the card so the real list can show
    fun showContent() {
        Log.d(TAG, "showContent")
        reset()
        visibility = View.GONE
    }

    private fun showIcon(@DrawableRes icon: Int, colourRes: Int) {
        binding.stateIcon.setImageResource(icon)
        binding.stateIcon.setColorFilter(
            androidx.core.content.ContextCompat.getColor(context, colourRes)
        )
        binding.stateIcon.visibility = View.VISIBLE
    }

    // clear everything so the next state start's clean
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
