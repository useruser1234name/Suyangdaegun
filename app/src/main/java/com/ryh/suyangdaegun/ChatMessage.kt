// ChatMessage.kt
package com.ryh.suyangdaegun

data class ChatMessage(
    val sender: String = "",
    val content: String = "",
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
