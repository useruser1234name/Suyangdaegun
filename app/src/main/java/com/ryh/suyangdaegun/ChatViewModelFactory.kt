// ChatViewModelFactory.kt
package com.ryh.suyangdaegun

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatViewModelFactory(private val chatRoomId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(chatRoomId) as T
        }
        throw IllegalArgumentException("알 수 없는 ViewModel 클래스")
    }
}
