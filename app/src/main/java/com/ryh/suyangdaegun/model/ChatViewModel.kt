package com.ryh.suyangdaegun.model

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// 🔹 채팅 메시지 데이터 클래스
data class ChatMessage(
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

// 🔹 채팅 관리 ViewModel
class ChatViewModel(private val chatRoomId: String, private val context: Context) : ViewModel() {
    private val database =
        FirebaseDatabase.getInstance().reference.child("chat_rooms").child(chatRoomId)
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> get() = _messages

    private val _gptResponse = MutableStateFlow<String>("")
    val gptResponse: StateFlow<String> get() = _gptResponse

    private val chatGptService = ChatGptService()

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _participantName = MutableStateFlow<String>("")
    val participantName: StateFlow<String> get() = _participantName

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ChatSettings", Context.MODE_PRIVATE)

    private val _fontSize = MutableStateFlow(16f)
    val fontSize: StateFlow<Float> get() = _fontSize

    private val _gptSuggestions = MutableStateFlow<List<String>>(emptyList())
    val gptSuggestions: StateFlow<List<String>> get() = _gptSuggestions

    init {
        getParticipantName()
        loadFontSize() // 🔥 앱 실행 시 저장된 글자 크기 불러오기

        // Firebase에서 메시지 실시간 감지
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

    // 🔹 저장된 글씨 크기 불러오기
    private fun loadFontSize() {
        _fontSize.value = prefs.getFloat("fontSize", 16f) // 🔥 SharedPreferences에서 불러오기
    }

    fun setFontSize(newSize: Float) {
        _fontSize.value = newSize
        prefs.edit().putFloat("fontSize", newSize).apply() // 🔥 변경 시 저장
    }

    private fun getParticipantName() {
        val currentUserUid = auth.currentUser?.uid ?: return

        firestore.collection("chat_rooms").document(chatRoomId).get()
            .addOnSuccessListener { document ->
                val participants = document.get("participants") as? List<String> ?: emptyList()
                val otherUserUid = participants.firstOrNull { it != currentUserUid }

                if (otherUserUid != null) {
                    firestore.collection("users").document(otherUserUid).get()
                        .addOnSuccessListener { userDoc ->
                            _participantName.value = userDoc.getString("nickname") ?: "알 수 없음"
                        }
                }
            }
    }

    // 🔹 메시지 전송
    fun sendMessage(content: String) {
        val senderId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val messageId = database.child("messages").push().key ?: return

        val message = ChatMessage(senderId, content, System.currentTimeMillis(), false)
        database.child("messages").child(messageId).setValue(message)
    }

    fun markMessagesAsRead() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        database.child("messages").get().addOnSuccessListener { snapshot ->
            val updates = mutableMapOf<String, Any>()

            snapshot.children.forEach { messageSnapshot ->
                val message = messageSnapshot.getValue(ChatMessage::class.java)
                val messageId = messageSnapshot.key

                if (message != null && message.senderId != currentUserUid && !message.isRead) {
                    updates["messages/$messageId/isRead"] = true
                }
            }

            if (updates.isNotEmpty()) {
                database.updateChildren(updates)
            }
        }
    }

    fun requestGptSuggestions(chatHistory: List<Map<String, String>>) {
        viewModelScope.launch {
            chatGptService.getMultipleResponses(chatHistory, "", callback = { suggestions ->
                _gptSuggestions.value = suggestions
            })
        }
    }
}
