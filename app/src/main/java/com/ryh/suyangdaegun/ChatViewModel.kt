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
    val currentUserUID: String = auth.currentUser?.uid.orEmpty()

    private val chatRoomId = "sampleChatRoom" // 채팅방 ID (예: 특정 사용자와의 고유한 ID)

    init {
        loadMessages()
    }

    /**
     * Firestore에서 메시지를 실시간으로 가져오는 함수
     */
    private fun loadMessages() {
        firestore.collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("ChatViewModel", "Error loading messages: ${e.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val newMessages = snapshot.documents.map { document ->
                        document.toObject(ChatMessage::class.java) ?: ChatMessage()
                    }
                    _messages.value = newMessages
                }
            }
    }


    /**
     * Firestore에 메시지를 저장하는 함수
     */
    fun sendMessage(content: String) {
        val message = ChatMessage(
            sender = currentUserUID,
            content = content,
            isRead = false,
            timestamp = System.currentTimeMillis()
        )

        firestore.collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")
            .add(message)
            .addOnSuccessListener {
            Log.d("ChatViewModel", "Message sent successfully")
        }
            .addOnFailureListener { e ->
                Log.e("ChatViewModel", "Error sending message: ${e.message}")
            }
    }

    /**
     * 메시지를 읽음 상태로 업데이트
     */
    fun markMessagesAsRead() {
        val batch = firestore.batch()
        firestore.collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")
            .whereEqualTo("isRead", false)
            .whereNotEqualTo("sender", currentUserUID)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    doc.reference.update("isRead", true)
                }
                batch.commit()
            }
            .addOnFailureListener { e ->
                Log.e("ChatViewModel", "Error updating read status: ${e.message}")
            }
    }

}