package com.ryh.suyangdaegun.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// ✅ `ChatViewModel`을 ViewModelProvider에서 생성하기 위한 Factory
class ChatViewModelFactory(private val chatRoomId: String, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(chatRoomId, context) as T // ✅ Context 추가하여 전달
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
