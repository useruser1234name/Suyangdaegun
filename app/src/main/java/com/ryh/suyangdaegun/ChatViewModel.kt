package com.ryh.suyangdaegun

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// 🔹 채팅 메시지 데이터 클래스
data class ChatMessage(
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// 🔹 채팅 관리 ViewModel
class ChatViewModel(private val chatRoomId: String) : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference.child("chat_rooms").child(chatRoomId)
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> get() = _messages

    init {
        // 🔹 메시지 변경 감지 후 자동 업데이트
        database.child("messages").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                message?.let {
                    _messages.value += it
                }
            }
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        })
    }

    // 🔹 메시지 전송
    fun sendMessage(content: String) {
        val senderId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val message = ChatMessage(senderId, content)
        database.child("messages").push().setValue(message)
    }
}
