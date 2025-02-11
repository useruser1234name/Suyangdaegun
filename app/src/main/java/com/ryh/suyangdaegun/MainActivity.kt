package com.ryh.suyangdaegun

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.ryh.suyangdaegun.auth.AuthManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 메인 네비게이션 실행 (여기서 AppNavigatorMain이 호출되어 BottomNavScreen 등 포함)
        setContent { AppNavigatorMain() }
    }
}

data class DummyUser(val name: String, val email: String)

@Composable
fun MainScreen(rootNavController: androidx.navigation.NavHostController) {
    val viewModel: MatchingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("오늘의 추천", modifier = Modifier.padding(bottom = 16.dp))
        val dummyCards = List(5) { index -> "추천 카드 ${index + 1}" }
        LazyColumn {
            items(dummyCards) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            val targetEmail = "a01062946631@gmail.com" // 매칭할 대상의 이메일

                            // 🔹 상대방 UID 찾기 -> 찾은 후 매칭 요청 전송
                            viewModel.findUserByEmail(targetEmail) { targetUid ->
                                if (targetUid != null) {
                                    viewModel.sendMatchRequest(
                                        targetUid = targetUid,
                                        targetEmail = targetEmail,  // ✅ targetEmail 추가
                                        onSuccess = { Log.d("Matching", "매칭 요청 성공!") },
                                        onFailure = { e -> Log.e("Matching", "매칭 요청 실패: ${e.message}") }
                                    )
                                } else {
                                    Log.e("Matching", "해당 이메일의 사용자를 찾을 수 없습니다.")
                                }
                            }
                        }
                ) {
                    Text(card, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}




//// Firebase Realtime Database에 매칭 요청 전송
//fun sendMatchRequest() {
//    val currentUser = FirebaseAuth.getInstance().currentUser ?: return
//    // 특정 사용자 이메일
//    val targetEmail = "a01062946631@gmail.com"
//    // 실제 환경에서는 targetEmail을 기반으로 대상 uid 조회 필요 (예제에서는 단순 처리)
//    val request = RequestEntry(
//        senderUid = currentUser.uid,
//        senderEmail = currentUser.email ?: "",
//        receiverUid = "target_uid", // 실제 대상 uid로 대체
//        receiverEmail = targetEmail,
//        receiverName = "타겟 사용자"
//    )
//    FirebaseDatabase.getInstance().getReference("matchRequests")
//        .push()
//        .setValue(request)
//}
//
//

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
    fun findUserByEmail(targetEmail: String, onResult: (String?) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("email", targetEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onResult(null) // 사용자를 찾지 못한 경우
                } else {
                    val targetUid = documents.documents[0].id
                    onResult(targetUid) // 찾은 UID 반환
                }
            }
            .addOnFailureListener { onResult(null) }
    }

    /**
     * 🔹 매칭 요청 전송 (중복 방지)
     */
    fun sendMatchRequest(targetUid: String, targetEmail: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val senderUid = auth.currentUser?.uid ?: return onFailure(Exception("User not authenticated"))
        val senderEmail = auth.currentUser?.email ?: return onFailure(Exception("No email found"))

        val requestId = "${senderUid}_$targetUid"

        firestore.collection("match_requests").document(requestId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onFailure(Exception("이미 요청을 보냈습니다."))
                } else {
                    val request = MatchRequest(senderUid, senderEmail, targetUid, targetEmail, "pending")

                    firestore.collection("match_requests")
                        .document(requestId)
                        .set(request)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onFailure(e) }
                }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }
}
