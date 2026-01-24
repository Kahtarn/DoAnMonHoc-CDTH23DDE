package com.example.clientchodientu.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.content.Context
import com.example.clientchodientu.ui.chat.DetailChatActivity
import android.R.drawable.ic_dialog_info
import com.example.clientchodientu.untils.TokenManager
import kotlin.apply
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty
import kotlin.collections.set
import kotlin.jvm.java
import kotlin.toString

class FirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        val roomMessages = mutableMapOf<String, MutableList<Pair<String, String>>>()

        // Pair<sender, message>
        private const val CHANNEL_ID = "chat_messages"
        private const val MAX_MESSAGES = 5
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "From: ${remoteMessage.from}")

        if (isUserOnChat()) {
            // NGƯỜI DÙNG ĐANG XEM TIN NHẮN RỒI -> KHÔNG HIỆN THÔNG BÁO NỮA
            Log.d("FCM", "dang chat, khong hien thong bao")
            return
        }


        // This triggers when a notification arrives from the Server
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("Message id ", remoteMessage.messageId.toString())
            val title = remoteMessage.data["title"] ?: "New Message"
            val body = remoteMessage.data["body"] ?: "You have a new message"
            val roomId = remoteMessage.data["roomId"] ?: "general"

            sendNotification(title, body, roomId)
        }
    }

    override fun onNewToken(token: String) {
        // IMPORTANT: This token identifies THIS phone.
        // You must send this token to your Spring Boot server and save it in the 'users' table.
        Log.d("FCM", "firebase token: $token")
        TokenManager.updateFCMToken(this,token)
    }

    private fun isUserOnChat(): Boolean {
        // Kiểm tra xem người dùng có đang mở đúng phòng chat đó không?
        val prefs = getSharedPreferences("AppStatus", MODE_PRIVATE)
        val isOnChat = prefs.getBoolean("IS_ON_CHAT", false)

        Log.d("is on chat", isOnChat.toString())

        return isOnChat
    }

    private fun sendNotification(title: String, messageBody: String, roomId: String) {

        val pendingIntent = createChatPendingIntent(roomId)
        val deletePendingIntent = createDeletePendingIntent(roomId)


        // Add new message to room history
        if (!roomMessages.containsKey(roomId)) {
            roomMessages[roomId] = mutableListOf()
        }
        roomMessages[roomId]?.add(Pair(title, messageBody))

        // Keep only last 5 messages
        if ((roomMessages[roomId]?.size ?: 0) > MAX_MESSAGES) {
            roomMessages[roomId]?.removeAt(0)
        }

        // Build inbox style notification (like Telegram)
        val inboxStyle = NotificationCompat.InboxStyle()

        // Add each message as a line
        roomMessages[roomId]?.forEach { (sender, message) ->
            inboxStyle.addLine(message)
        }

        // Set summary (shown when collapsed)
        val messageCount = roomMessages[roomId]?.size ?: 0
        inboxStyle.setSummaryText("$messageCount messages")

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(ic_dialog_info)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDeleteIntent(deletePendingIntent) // Clear when dismissed
            .setStyle(inboxStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setGroup(roomId)

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create Channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Chat Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Use roomId for notification grouping
        notificationManager.notify(roomId.hashCode(), notificationBuilder.build())
    }

    private fun createChatPendingIntent(roomId: String): PendingIntent {
        val intent = Intent(this, DetailChatActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("roomId", roomId)
        }
        return PendingIntent.getActivity(
            this,
            roomId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createDeletePendingIntent(roomId: String): PendingIntent {
        val intent = Intent(this, NotificationDismissReceiver::class.java).apply {
            putExtra("roomId", roomId)
        }
        return PendingIntent.getBroadcast(
            this,
            roomId.hashCode() + 1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }


}
