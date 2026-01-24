package com.example.clientchodientu.adapter

import com.example.clientchodientu.entity.ChatMessage

interface OnMessageLongClickListener {
     fun onRevokeMessage(message: ChatMessage, position: Int)
     fun onCopyMessage(message: ChatMessage)
}