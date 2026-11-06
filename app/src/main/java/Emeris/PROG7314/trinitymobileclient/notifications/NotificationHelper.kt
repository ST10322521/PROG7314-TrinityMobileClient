package Emeris.PROG7314.trinitymobileclient.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import Emeris.PROG7314.trinitymobileclient.R

/**
 * Utility object for displaying local push notifications triggered by SignalR hub events.
 */
object NotificationHelper {

    private const val TAG = "NotificationHelper"
    const val CHANNEL_ID_ALERTS   = "trinity_alerts"
    const val CHANNEL_ID_EVENTS   = "trinity_events"
    private const val CHANNEL_NAME_ALERTS = "Agent Alerts"
    private const val CHANNEL_NAME_EVENTS = "Agent Events"
    private const val CHANNEL_DESC_ALERTS = "Critical agent alert notifications"
    private const val CHANNEL_DESC_EVENTS = "General agent event notifications"
    @Volatile
    private var nextId = 1000
    private fun nextNotificationId(): Int = synchronized(this) { nextId++ }

    /**
     * Creates the notification channels
     * @param context Application context
     */
    fun initialize(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID_ALERTS,
                    CHANNEL_NAME_ALERTS,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = CHANNEL_DESC_ALERTS
                    enableVibration(true)
                }
            )

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID_EVENTS,
                    CHANNEL_NAME_EVENTS,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = CHANNEL_DESC_EVENTS
                }
            )

            Log.d(TAG, "Notification channels created")
        }
    }

    /**
     * Show alert notification (critical agent events).
     *
     * @param context Application or Activity context
     * @param title   Notification title
     * @param message Notification body text (supports long text via BigText style)
     * @param intent  Optional tap action — the Activity to open on tap
     * @return The notification ID, can be used to cancel it later
     */
    fun showAlert(
        context: Context,
        title: String,
        message: String,
        intent: Intent? = null
    ): Int = post(
        context    = context,
        title      = title,
        message    = message,
        channelId  = CHANNEL_ID_ALERTS,
        priority   = NotificationCompat.PRIORITY_HIGH,
        intent     = intent
    )

    /**
     * Show a event notification (general agent status updates).
     *
     * @param context Application or Activity context
     * @param title   Notification title
     * @param message Notification body text
     * @param intent  Optional tap action
     * @return The notification ID, can be used to cancel it later
     */
    fun showEvent(
        context: Context,
        title: String,
        message: String,
        intent: Intent? = null
    ): Int = post(
        context    = context,
        title      = title,
        message    = message,
        channelId  = CHANNEL_ID_EVENTS,
        priority   = NotificationCompat.PRIORITY_DEFAULT,
        intent     = intent
    )

    /**
     * Cancel a previously shown notification by its ID.
     *
     * @param context        Application or Activity context
     * @param notificationId The ID returned by [showAlert] or [showEvent]
     */
    fun cancel(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
        Log.d(TAG, "Cancelled notification id=$notificationId")
    }

    /** Cancel all notifications. */
    fun cancelAll(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
        Log.d(TAG, "Cancelled all notifications")
    }

    // Internal
    @Suppress("MissingPermission")
    private fun post(
        context: Context,
        title: String,
        message: String,
        channelId: String,
        priority: Int,
        intent: Intent?
    ): Int {
        val pendingIntent = intent?.let {
            PendingIntent.getActivity(
                context,
                nextNotificationId(),
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_bell)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(priority)
            .setAutoCancel(true)
            .apply { pendingIntent?.let { pi -> setContentIntent(pi) } }
            .build()

        val id = nextNotificationId()
        NotificationManagerCompat.from(context).notify(id, notification)
        Log.d(TAG, "Posted notification id=$id title='$title'")
        return id
    }
}
