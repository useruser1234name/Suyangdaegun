package com.ryh.suyangdaegun

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// 🔹 ChatViewModel을 ViewModelProvider에서 생성하기 위한 Factory
class ChatViewModelFactory(private val chatRoomId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(chatRoomId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
