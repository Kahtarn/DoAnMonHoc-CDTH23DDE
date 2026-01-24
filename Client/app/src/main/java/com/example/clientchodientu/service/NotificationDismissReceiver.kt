package com.example.emailotp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val roomId = intent.getStringExtra("roomId")
        if (roomId != null) {
            // Clear message history for this room
            FirebaseMessagingService.roomMessages.remove(roomId)
        }
    }
}