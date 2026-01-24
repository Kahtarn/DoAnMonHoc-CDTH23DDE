package com.example.clientchodientu.dto.chat

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class InboxRespond(
    val partnerId: Int =0,
    val partnerName: String = "",
    val avatarUrl: String? = null,
    var lastMessage: String = "",
    val time: Any? = "",
    var roomName: String = "",
    @get:PropertyName("isRevoke")
    @set:PropertyName("isRevoke")
    var isRevoke : Boolean = false
)
