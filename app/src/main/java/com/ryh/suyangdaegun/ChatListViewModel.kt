package com.ryh.suyangdaegun

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// 🔹 채팅방 데이터 클래스
data class ChatRoomItem(
    val chatRoomId: String, //채팅방 uid
    val participantName: String, // 반대편 사용자 이름만 표시 fun getParticipantName
    val lastMessage: String // 마지막 메시지 fun getLastMessageFromRealtimeDB
)
//리스트 카드 -> 대화참여자(상대편), 마지막 메시지 표시

// 🔹 채팅방 리스트 관리 ViewModel
class ChatListViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val database = FirebaseDatabase.getInstance().reference.child("chat_rooms")
    private val auth = FirebaseAuth.getInstance()
    private val _chatRooms = MutableStateFlow<List<ChatRoomItem>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoomItem>> get() = _chatRooms

    init {
        loadChatRooms()
    }

    private fun loadChatRooms() {
        val currentUserUid = auth.currentUser?.uid ?: return

        firestore.collection("chat_rooms")
            .whereArrayContains("participants", currentUserUid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                val chatRoomsList = mutableListOf<ChatRoomItem>()

                snapshot?.documents?.forEach { document ->
                    val chatRoomId = document.id
                    val participants = document.get("participants") as? List<String> ?: emptyList()

                    //  현재 유저를 제외한 상대방 UID만 가져오기
                    val otherUserUid = participants.firstOrNull { it != currentUserUid }

                    if (otherUserUid != null) {
                        getLastMessageFromRealtimeDB(chatRoomId) { lastMessage ->
                            getParticipantName(otherUserUid) { participantName ->
                                chatRoomsList.add(
                                    ChatRoomItem(
                                        chatRoomId = chatRoomId,
                                        participantName = participantName,
                                        lastMessage = lastMessage ?: "메시지가 없습니다."
                                    )
                                )
                                _chatRooms.value = chatRoomsList
                            }
                        }
                    }
                }
            }
    }

    // 🔹 최신 메시지 가져오기 (Realtime Database 사용)
    private fun getLastMessageFromRealtimeDB(chatRoomId: String, callback: (String?) -> Unit) {
        database.child(chatRoomId).child("messages")
            .orderByChild("timestamp")
            .limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lastMessage = snapshot.children.firstOrNull()?.child("message")?.getValue(String::class.java)
                    callback(lastMessage)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    // 🔹 상대방 닉네임 가져오기
    private fun getParticipantName(userUid: String, callback: (String) -> Unit) {
        firestore.collection("users").document(userUid).get()
            .addOnSuccessListener { snapshot ->
                val nickname = snapshot.getString("nickname") ?: "알 수 없음"
                callback(nickname)
            }
    }
}
