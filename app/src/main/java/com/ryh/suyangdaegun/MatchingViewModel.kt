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
     *  이메일을 UID로 변환하는 함수 -> uid로 검색 시 보안 문제 발생 할 수 있게 때문에 사용자 구글 메일 주소 이용하여 매칭
     *  매칭은 파이어 베이스 아닌 관상,사주 기반 매칭이기 때문에 flask mongodb같은  매칭 모델 구현하여 연결 필요   -> targetEmail
     *  매칭을 기반으로 채팅방을 구성하기에 현재는 1:1 통신만 가능
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
     * 🔹 매칭 요청 Firestore에 저장 (중복 제거) a -> b 매칭 요청 시 accepted or pending 상태 일때
     * 중복 요청 가능성 있기 때문에 중복 요청 방지
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
     * 🔹 매칭 요청 취소 기능 추가 (본인 -> 타 사용자에게 매칭 요청 했을 때 취소 가능)
     */
    fun cancelMatchRequest(request: MatchRequest) {
        firestore.collection("match_requests")
            .document("${request.senderUid}_${request.receiverUid}")
            .delete() //-> 요청 시 생성된 match 문서 삭제
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
     * 매칭 reject -> 파이어 베이스 match_requests 에서 문서 삭제
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
