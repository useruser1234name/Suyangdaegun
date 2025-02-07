package com.ryh.suyangdaegun

import android.os.Message
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> get() = _messages

    private val chatRoomId = "exampleChatRoomId" // 고유 채팅방 ID

    init {
        loadMessages()
    }

    fun loadMessages() {
        firestore.collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("ChatViewModel", "Error loading messages: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val newMessages = snapshot.documents.map { it.toObject(ChatMessage::class.java)!! }
                    _messages.value = newMessages
                }
            }
    }

    fun sendMessage(content: String) {
        val message = ChatMessage(
            sender = auth.currentUser?.uid ?: "",
            content = content
        )

        firestore.collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")
            .add(message)
            .addOnSuccessListener {
                Log.d("ChatViewModel", "Message sent")
            }
            .addOnFailureListener { e ->
                Log.e("ChatViewModel", "Error sending message: ${e.message}")
            }
    }

    fun markMessagesAsRead() {
        firestore.collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")
            .whereEqualTo("isRead", false)
            .whereNotEqualTo("sender", auth.currentUser?.uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = firestore.batch()
                for (doc in snapshot.documents) {
                    batch.update(doc.reference, "isRead", true)
                }
                batch.commit()
            }
    }
}
