package Emeris.PROG7314.trinitymobileclient.signalr

import android.util.Log
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Modular singleton for managing a SignalR hub connection.
 *
 * Typical usage:
 * ```kotlin
 * val manager = SignalRManager.getInstance()
 *
 * // Connect (optionally with a bearer token provider, e.g. Firebase ID token)
 * manager.connect(
 *     hubUrl = "",
 *     tokenProvider = { firebaseUser.getIdToken(false).result?.token ?: "" },
 *     onConnected = {  },
 *     onError    = { e -> Log.e("TAG", "Connection failed", e) }
 * )
 *
 * // Subscribe to a typed hub method
 * manager.on("AgentStatusChanged", AgentStatusDto::class.java) { dto ->
 *     NotificationHelper.showAlert(context, "Agent Update", dto.message)
 * }
 *
 * // Fire-and-forget invoke
 * manager.invoke("SendCommand", "agent-id", "execute")
 *
 * // Observe connection state
 * manager.connectionState.collect { state -> ... }
 *
 * // Disconnect when done
 * manager.disconnect()
 * ```
 */
class SignalRManager private constructor() {

    // ── Connection state ──────────────────────────────────────────────────────

    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        RECONNECTING
    }

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)

    /** Observe the hub connection state reactively (e.g. in a ViewModel). */
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    val isConnected: Boolean
        get() = _connectionState.value == ConnectionState.CONNECTED

    // ── Internal hub ──────────────────────────────────────────────────────────

    private var hubConnection: HubConnection? = null

    // ── Singleton ─────────────────────────────────────────────────────────────

    companion object {
        private const val TAG = "SignalRManager"

        @Volatile
        private var instance: SignalRManager? = null

        fun getInstance(): SignalRManager =
            instance ?: synchronized(this) {
                instance ?: SignalRManager().also { instance = it }
            }
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Connect to a SignalR hub endpoint.
     *
     * @param hubUrl       Full URL to the hub (e.g. "https://api.example.com/hubs/agents")
     * @param tokenProvider Optional lambda that returns a bearer token on each reconnect.
     *                      Use this to inject Firebase ID tokens or JWT tokens.
     * @param onConnected   Called on the RxJava completion thread when the connection succeeds.
     * @param onError       Called with the [Throwable] if the connection attempt fails.
     */
    fun connect(
        hubUrl: String,
        group: String? = null,
        tokenProvider: (() -> String)? = null,
        onConnected: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        if (_connectionState.value == ConnectionState.CONNECTED ||
            _connectionState.value == ConnectionState.CONNECTING) {
            Log.w(TAG, "Already connected or connecting — ignoring duplicate connect call")
            return
        }

        val builder = HubConnectionBuilder.create(hubUrl)

        tokenProvider?.let { provider ->
            builder.withAccessTokenProvider(
                Single.defer { Single.just(provider.invoke()) }
            )
        }

        hubConnection = builder.build().also { conn ->
            conn.onClosed { error ->
                _connectionState.value = ConnectionState.DISCONNECTED
                if (error != null) {
                    Log.e(TAG, "Hub connection closed with error", error)
                } else {
                    Log.d(TAG, "Hub connection closed cleanly")
                }
            }
        }

        _connectionState.value = ConnectionState.CONNECTING
        Log.d(TAG, "Connecting to hub: $hubUrl")

        hubConnection!!.start()
            .subscribe(
                {
                    _connectionState.value = ConnectionState.CONNECTED
                    Log.d(TAG, "Connected to hub: $hubUrl")
                    group?.let { joinGroup(it) }
                    onConnected?.invoke()
                },
                { error ->
                    _connectionState.value = ConnectionState.DISCONNECTED
                    Log.e(TAG, "Failed to connect to hub: $hubUrl", error)
                    onError?.invoke(error)
                }
            )
    }

    /**
     * Gracefully stop the hub connection.
     *
     * @param onDisconnected Called when the disconnect completes.
     */
    fun disconnect(onDisconnected: (() -> Unit)? = null) {
        val conn = hubConnection ?: run {
            Log.w(TAG, "disconnect() called but no active connection")
            return
        }
        conn.stop().subscribe(
            {
                _connectionState.value = ConnectionState.DISCONNECTED
                hubConnection = null
                Log.d(TAG, "Disconnected from hub")
                onDisconnected?.invoke()
            },
            { error ->
                Log.e(TAG, "Error during hub disconnect", error)
            }
        )
    }

    // ── Group management ──────────────────────────────────────────────────────

    /**
     * Join a SignalR group.
     *
     * The server hub must expose a method (default name: `"JoinGroup"`) that calls
     * `Groups.AddToGroupAsync(Context.ConnectionId, groupName)`.
     *
     * @param groupName       The group name to join
     * @param joinMethodName  Server hub method to call (override if yours is named differently)
     */
    fun joinGroup(
        groupName: String,
        joinMethodName: String = "JoinGroup",
        onComplete: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        Log.d(TAG, "Joining group: '$groupName'")
        invoke(joinMethodName, groupName, onComplete, onError)
    }

    /**
     * Leave a SignalR group.
     *
     * The server hub must expose a method (default name: `"LeaveGroup"`) that calls
     * `Groups.RemoveFromGroupAsync(Context.ConnectionId, groupName)`.
     *
     * @param groupName        The group name to leave
     * @param leaveMethodName  Server hub method to call (override if yours is named differently)
     */
    fun leaveGroup(
        groupName: String,
        leaveMethodName: String = "LeaveGroup",
        onComplete: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        Log.d(TAG, "Leaving group: '$groupName'")
        invoke(leaveMethodName, groupName, onComplete, onError)
    }

    // ── Agent terminal subscription ─────────────────────────────────────────

    /**
     * Subscribe to a specific agent's terminal stream.
     *
     * Calls `SubscribeToAgent(agentID)` on the hub, which adds this
     * connection to the agent's group. The server will then push
     * `TaskNotification` messages to this client.
     *
     * Call this when the user opens an agent's terminal tab.
     *
     * @param agentId      The agent ID to subscribe to
     * @param onSubscribed Called when the invoke completes successfully
     * @param onError      Called if the invoke fails
     */
    fun subscribeToAgent(
        agentId: String,
        onSubscribed: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        Log.d(TAG, "Subscribing to agent terminal: agentId='$agentId'")
        invoke("SubscribeToAgent", agentId, onSubscribed, onError)
    }

    /**
     * Unsubscribe from a specific agent's terminal stream.
     *
     * Calls `UnsubscribeFromAgent(agentID)` on the hub, removing this
     * connection from the agent's group.
     *
     * Call this when the user closes or navigates away from the agent's terminal tab.
     *
     * @param agentId        The agent ID to unsubscribe from
     * @param onUnsubscribed Called when the invoke completes successfully
     * @param onError        Called if the invoke fails
     */
    fun unsubscribeFromAgent(
        agentId: String,
        onUnsubscribed: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        Log.d(TAG, "Unsubscribing from agent terminal: agentId='$agentId'")
        invoke("UnsubscribeFromAgent", agentId, onUnsubscribed, onError)
    }

    /**
     * Subscribe to the event log stream for a campaign.
     *
     * Invokes `SubscribeToEventLog` on the hub with [campaignId],
     * which adds this connection to the campaign's group.
     *
     * @param campaignId     The campaign ID to subscribe to
     * @param onSubscribed   Called when the server invoke completes successfully
     * @param onError        Called if the invoke fails
     */
    fun subscribeToEventLog(
        campaignId: String,
        onSubscribed: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        Log.d(TAG, "Subscribing to event log: campaignId='$campaignId'")
        invoke("SubscribeToEventLog", campaignId, onSubscribed, onError)
    }

    /**
     * Unsubscribe from the event log stream for a campaign.
     *
     * Invokes `UnsubscribeFromEventLog` on the hub with [campaignId],
     * which removes this connection from the campaign's group.
     *
     * @param campaignId       The campaign ID to unsubscribe from
     * @param onUnsubscribed   Called when the server invoke completes successfully
     * @param onError          Called if the invoke fails
     */
    fun unsubscribeFromEventLog(
        campaignId: String,
        onUnsubscribed: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        Log.d(TAG, "Unsubscribing from event log: campaignId='$campaignId'")
        invoke("UnsubscribeFromEventLog", campaignId, onUnsubscribed, onError)
    }

    // ── Receiving messages ────────────────────────────────────────────────────

    /**
     * Register a handler for a hub push method with **one typed argument**.
     *
     * Example:
     * ```kotlin
     * manager.on("AgentOutput", String::class.java) { output ->
     *     NotificationHelper.showEvent(context, "Agent", output)
     * }
     * ```
     *
     * @param methodName Hub method name the server calls (must match the C# hub method / client proxy name)
     * @param type       Runtime class of the expected argument (use `MyDto::class.java` for objects)
     * @param handler    Invoked on the RxJava IO thread when the message arrives
     */
    fun <T> on(methodName: String, type: Class<T>, handler: (T) -> Unit) {
        ensureConnected(methodName) ?: return
        hubConnection!!.on(methodName, { value -> handler(value) }, type)
        Log.d(TAG, "Registered handler for hub method: '$methodName'")
    }

    /**
     * Register a handler for a hub push method with **no arguments**.
     *
     * Example:
     * ```kotlin
     * manager.onEmpty("SessionExpired") {
     *     // navigate back to login
     * }
     * ```
     */
    fun onEmpty(methodName: String, handler: () -> Unit) {
        ensureConnected(methodName) ?: return
        hubConnection!!.on(methodName) { handler() }
        Log.d(TAG, "Registered empty handler for hub method: '$methodName'")
    }

    // ── Sending messages ──────────────────────────────────────────────────────

    /**
     * Fire-and-forget invoke of a hub method with **no arguments**.
     */
    fun invoke(
        methodName: String,
        onComplete: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        if (!ensureConnectedForSend(methodName, onError)) return
        try {
            hubConnection!!.send(methodName)
            Log.d(TAG, "Invoked: '$methodName'")
            onComplete?.invoke()
        } catch (e: Exception) {
            Log.e(TAG, "Invoke failed: '$methodName'", e)
            onError?.invoke(e)
        }
    }

    /**
     * Fire-and-forget invoke with **one argument**.
     */
    fun invoke(
        methodName: String,
        arg1: Any,
        onComplete: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        if (!ensureConnectedForSend(methodName, onError)) return
        try {
            hubConnection!!.send(methodName, arg1)
            Log.d(TAG, "Invoked: '$methodName'")
            onComplete?.invoke()
        } catch (e: Exception) {
            Log.e(TAG, "Invoke failed: '$methodName'", e)
            onError?.invoke(e)
        }
    }

    /**
     * Fire-and-forget invoke with **two arguments**.
     */
    fun invoke(
        methodName: String,
        arg1: Any,
        arg2: Any,
        onComplete: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        if (!ensureConnectedForSend(methodName, onError)) return
        try {
            hubConnection!!.send(methodName, arg1, arg2)
            Log.d(TAG, "Invoked: '$methodName'")
            onComplete?.invoke()
        } catch (e: Exception) {
            Log.e(TAG, "Invoke failed: '$methodName'", e)
            onError?.invoke(e)
        }
    }

    /**
     * Invoke a hub method and **expect a return value** of type [R].
     *
     * Example:
     * ```kotlin
     * manager.invokeWithReturn(
     *     returnType  = AgentStatusDto::class.java,
     *     methodName  = "GetAgentStatus",
     *     arg1        = agentId,
     *     onResult    = { dto -> updateUi(dto) },
     *     onError     = { e -> showError(e.message) }
     * )
     * ```
     */
    fun <R : Any> invokeWithReturn(
        returnType: Class<R>,
        methodName: String,
        arg1: Any? = null,
        onResult: (R) -> Unit,
        onError: ((Throwable) -> Unit)? = null
    ) {
        if (!ensureConnectedForSend(methodName, onError)) return
        val single: Single<R> = if (arg1 != null) {
            hubConnection!!.invoke(returnType, methodName, arg1)
        } else {
            hubConnection!!.invoke(returnType, methodName)
        }
        single.subscribe(
            { result -> Log.d(TAG, "invokeWithReturn '$methodName' got result"); onResult(result) },
            { e -> Log.e(TAG, "invokeWithReturn '$methodName' failed", e); onError?.invoke(e) }
        )
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun ensureConnected(context: String): Unit? =
        if (!isConnected) {
            Log.e(TAG, "Not connected — cannot register handler for '$context'")
            null
        } else Unit

    private fun ensureConnectedForSend(
        methodName: String,
        onError: ((Throwable) -> Unit)?
    ): Boolean {
        if (!isConnected) {
            val msg = "Not connected — cannot invoke '$methodName'"
            Log.e(TAG, msg)
            onError?.invoke(IllegalStateException(msg))
            return false
        }
        return true
    }
}
