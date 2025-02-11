package com.ryh.suyangdaegun

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore



@Composable
fun MatchingScreen(navController: NavHostController) {
    var receivedRequests by remember { mutableStateOf(listOf<MatchRequest>()) }
    val currentUser = FirebaseAuth.getInstance().currentUser

    val dbRef = FirebaseFirestore.getInstance().collection("match_requests")
        .whereEqualTo("receiverUid", currentUser?.uid)

    DisposableEffect(Unit) {
        val listener = dbRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("MatchingScreen", "Error loading requests", e)
                return@addSnapshotListener
            }

            receivedRequests = snapshot?.documents?.mapNotNull { it.toObject(MatchRequest::class.java) } ?: emptyList()
        }
        onDispose { listener.remove() }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("내가 받은 요청", modifier = Modifier.padding(bottom = 8.dp))

        LazyColumn {
            items(receivedRequests) { request ->
                ReceivedRequestItem(
                    request = request,
                    onAccept = {
                        acceptMatchRequest(request, navController)
                    },
                    onReject = {
                        rejectMatchRequest(request)
                    }
                )
            }
        }
    }
}


fun acceptMatchRequest(request: MatchRequest, navController: NavHostController) {
    val firestore = FirebaseFirestore.getInstance()
    val chatRoomId = generateChatRoomId(request)

    firestore.collection("match_requests")
        .document("${request.senderUid}_${request.receiverUid}") // ✅ 요청 삭제
        .delete()
        .addOnSuccessListener {
            Log.d("Matching", "매칭 요청 수락 완료")

            // ✅ 채팅방 생성
            val chatRoom = mapOf(
                "participants" to listOf(request.senderUid, request.receiverUid),
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("chat_rooms")
                .document(chatRoomId)
                .set(chatRoom)
                .addOnSuccessListener {
                    Log.d("Matching", "채팅방 생성 완료: $chatRoomId")

                    // ✅ 채팅방으로 이동
                    navController.navigate("chatting/$chatRoomId")
                }
                .addOnFailureListener { e -> Log.e("Matching", "채팅방 생성 실패", e) }
        }
}

fun rejectMatchRequest(request: MatchRequest) {
    FirebaseFirestore.getInstance().collection("match_requests")
        .document("${request.senderUid}_${request.receiverUid}") // ✅ 요청 삭제
        .delete()
        .addOnSuccessListener { Log.d("Matching", "매칭 요청 거절 완료") }
}

fun generateChatRoomId(request: MatchRequest): String {
    return if (request.senderUid < request.receiverUid) {
        "${request.senderUid}_${request.receiverUid}"
    } else {
        "${request.receiverUid}_${request.senderUid}"
    }
}


@Composable
fun ReceivedRequestItem(
    request: MatchRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "From: ${request.senderEmail}",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true } // ✅ 리스트 아이템 클릭 시 다이얼로그 표시
                .padding(16.dp)
        )

        // ✅ 수락/거절 다이얼로그
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("매칭 요청") },
                text = { Text("${request.senderEmail}님의 요청을 수락하시겠습니까?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            onAccept() // ✅ 수락 동작 실행
                        }
                    ) { Text("수락") }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            onReject() // ✅ 거절 동작 실행
                        }
                    ) { Text("거절") }
                }
            )
        }
    }
}

// 수락 처리 (예시: 요청 삭제)

