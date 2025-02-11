package com.ryh.suyangdaegun

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// 🔹 채팅방 데이터 클래스
data class ChatRoom(
    val chatRoomId: String = "",
    val participants: List<String> = emptyList()
)

// 🔹 채팅방 리스트 관리 ViewModel
class ChatListViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoom>> get() = _chatRooms

    init {
        loadChatRooms()
    }

    // 🔹 현재 사용자가 포함된 채팅방 목록 가져오기
    private fun loadChatRooms() {
        val currentUserUid = auth.currentUser?.uid ?: return

        firestore.collection("chat_rooms")
            .whereArrayContains("participants", currentUserUid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                _chatRooms.value = snapshot?.toObjects(ChatRoom::class.java) ?: emptyList()
            }
    }
}
