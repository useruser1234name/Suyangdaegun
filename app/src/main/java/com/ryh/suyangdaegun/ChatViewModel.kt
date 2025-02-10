// ChatViewModel.kt
package com.ryh.suyangdaegun

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel(private val chatRoomId: String) : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val chatRef: DatabaseReference = database.getReference("chatRooms")
        .child(chatRoomId)
        .child("messages")

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> get() = _messages

    private val messagesListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val messagesList = mutableListOf<ChatMessage>()
            for (child in snapshot.children) {
                child.getValue(ChatMessage::class.java)?.let { messagesList.add(it) }
            }
            _messages.value = messagesList.sortedBy { it.timestamp }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("ChatViewModel", "메시지 로드 오류: ${error.message}")
        }
    }

    init {
        chatRef.orderByChild("timestamp").addValueEventListener(messagesListener)
    }

    override fun onCleared() {
        super.onCleared()
        chatRef.removeEventListener(messagesListener)
    }

    fun sendMessage(content: String) {
        val message = ChatMessage(
            sender = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            content = content
        )
        chatRef.push().setValue(message)
            .addOnSuccessListener { Log.d("ChatViewModel", "메시지 전송 성공") }
            .addOnFailureListener { e -> Log.e("ChatViewModel", "메시지 전송 오류: ${e.message}") }
    }
}

//    fun markMessagesAsRead() {
//        firestore.collection("chatRooms")
//            .document(chatRoomId)
//            .collection("messages")
//            .whereEqualTo("isRead", false)
//            .whereNotEqualTo("sender", auth.currentUser?.uid)
//            .get()
//            .addOnSuccessListener { snapshot ->
//                val batch = firestore.batch()
//                for (doc in snapshot.documents) {
//                    batch.update(doc.reference, "isRead", true)
//                }
//                batch.commit()
//            }
//    }
