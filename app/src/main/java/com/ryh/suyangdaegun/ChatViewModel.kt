package com.ryh.suyangdaegun

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ChatMessage(
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel(private val chatRoomId: String) : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference.child("chat_rooms").child(chatRoomId)
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> get() = _messages

    init {
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

    fun sendMessage(content: String) {
        val senderId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val message = ChatMessage(senderId, content)
        database.child("messages").push().setValue(message)
    }
}
