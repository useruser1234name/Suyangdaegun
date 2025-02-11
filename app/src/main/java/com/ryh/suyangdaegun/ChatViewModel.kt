package com.ryh.suyangdaegun

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ChatMessage(
    val sender: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel(private val chatRoomId: String) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> get() = _messages

    init {
        firestore.collection("chat_rooms")
            .document(chatRoomId)
            .collection("messages")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("ChatViewModel", "Failed to load messages", e)
                    return@addSnapshotListener
                }

                val messagesList = snapshot?.documents?.mapNotNull { it.toObject(ChatMessage::class.java) } ?: emptyList()
                _messages.value = messagesList.sortedBy { it.timestamp }
            }
    }

    fun sendMessage(content: String) {
        val message = ChatMessage(
            sender = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            content = content
        )

        firestore.collection("chat_rooms")
            .document(chatRoomId)
            .collection("messages")
            .add(message)
            .addOnFailureListener { e ->
                Log.e("ChatViewModel", "Failed to send message: ${e.message}")
            }
    }
}
