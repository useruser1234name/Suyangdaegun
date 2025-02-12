package com.ryh.suyangdaegun

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

// 🔹 매칭 요청 데이터 클래스
data class MatchRequest(
    val senderUid: String = "",
    val senderEmail: String = "",
    val receiverUid: String = "",
    val receiverEmail: String = "",
    val status: String = "pending", // "pending", "accepted", "rejected"
    val timestamp: Long = System.currentTimeMillis()
)


class MatchingViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * 🔹 이메일을 UID로 변환하는 함수
     */
    fun getUserUidByEmail(targetEmail: String, callback: (String?) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("email", targetEmail)
            .get()
            .addOnSuccessListener { documents ->
                val targetUid = documents.documents.firstOrNull()?.id
                callback(targetUid)
            }
            .addOnFailureListener { callback(null) }
    }

    /**
     * 🔹 매칭 요청 Firestore에 저장 (중복 제거)
     */
    fun sendMatchRequestToFirestore(targetUid: String, callback: (Boolean) -> Unit) {
        val senderUid = auth.currentUser?.uid ?: return callback(false)
        val senderEmail = auth.currentUser?.email ?: return callback(false)
        val requestId = "${senderUid}_$targetUid"

        firestore.collection("match_requests").document(requestId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    callback(false) //  이미 요청이 존재
                } else {
                    val request = MatchRequest(
                        senderUid = senderUid,
                        senderEmail = senderEmail,
                        receiverUid = targetUid,
                        receiverEmail = "",
                        status = "pending"
                    )

                    firestore.collection("match_requests").document(requestId)
                        .set(request)
                        .addOnSuccessListener { callback(true) }
                        .addOnFailureListener { callback(false) }
                }
            }
            .addOnFailureListener { callback(false) }
    }

    /**
     * 🔹 내가 받은 매칭 요청 목록 가져오기
     */
    fun loadReceivedRequests(userUid: String, callback: (List<MatchRequest>) -> Unit) {
        firestore.collection("match_requests")
            .whereEqualTo("receiverUid", userUid)
            .whereEqualTo("status", "pending") //  "pending" 상태만 가져옴
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                callback(snapshot?.toObjects(MatchRequest::class.java) ?: emptyList())
            }
    }

    /**
     * 🔹 내가 보낸 매칭 요청 목록 가져오기
     */
    fun loadSentRequests(userUid: String, callback: (List<MatchRequest>) -> Unit) {
        firestore.collection("match_requests")
            .whereEqualTo("senderUid", userUid)
            .whereEqualTo("status", "pending") //  "pending" 상태만 가져옴
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                callback(snapshot?.toObjects(MatchRequest::class.java) ?: emptyList())
            }
    }

    /**
     * 🔹 매칭 요청 취소 기능 추가
     */
    fun cancelMatchRequest(request: MatchRequest) {
        firestore.collection("match_requests")
            .document("${request.senderUid}_${request.receiverUid}")
            .delete()
            .addOnSuccessListener { Log.d("Matching", "매칭 요청이 취소되었습니다.") }
            .addOnFailureListener { Log.e("Matching", "매칭 요청 취소 실패", it) }
    }


    /**
     * 🔹 매칭 요청 수락 (채팅방 생성)
     */
    fun approveMatchRequest(request: MatchRequest, callback: (String) -> Unit) {
        val chatRoomId = UUID.randomUUID().toString()

        val matchRequestRef = firestore.collection("match_requests")
            .document("${request.senderUid}_${request.receiverUid}")

        val chatRoomRef = firestore.collection("chat_rooms").document(chatRoomId)

        firestore.runTransaction { transaction ->
            transaction.update(matchRequestRef, "status", "accepted") //  상태 변경

            val chatRoom = mapOf(
                "chatRoomId" to chatRoomId,
                "participants" to listOf(request.senderUid, request.receiverUid),
                "createdAt" to System.currentTimeMillis()
            )
            transaction.set(chatRoomRef, chatRoom)
        }.addOnSuccessListener {
            callback(chatRoomId)
        }.addOnFailureListener { e ->
            Log.e("approveMatchRequest", "채팅방 생성 실패", e)
        }
    }

    /**
     * 🔹 매칭 요청 거절
     */
    fun declineMatchRequest(request: MatchRequest) {
        firestore.collection("match_requests")
            .document("${request.senderUid}_${request.receiverUid}")
            .update("status", "rejected") //  거절 상태 변경
    }
    /**
     * 🔹 채팅방 ID 생성
     */
    private fun createChatRoomId(senderUid: String, receiverUid: String): String {
        return if (senderUid < receiverUid) {
            "${senderUid}_${receiverUid}"
        } else {
            "${receiverUid}_${senderUid}"
        }
    }
}
