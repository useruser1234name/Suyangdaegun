package com.ryh.suyangdaegun

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.ryh.suyangdaegun.auth.AuthManager

class MainActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authManager = (application as SuyangdaegunApp).authManager

        val googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    authManager.handleSignInResult(
                        data = result.data,
                        onSuccess = { isExistingUser, uid ->
                            if (!isExistingUser) {
                                navigateToAccession(uid)
                            }
                        },
                        onFailure = { e -> Log.e("MainActivity", "로그인 실패", e) }
                    )
                } else {
                    Log.e("MainActivity", "Google 로그인 취소됨")
                }
            }
        setContent {
            AppNavigator(googleSignInLauncher, authManager)
        }
    }

    private fun navigateToAccession(uid: String?) {
        if (uid.isNullOrEmpty()) {
            Log.e("MainActivity", "회원가입 화면으로 이동하려 했으나 UID가 없음")
            return
        }
        val intent = Intent(this, AccessionActivity::class.java).apply {
            putExtra("uid", uid)
        }
        startActivity(intent)
        finish()
    }
}

data class DummyUser(val name: String, val email: String)

@Composable
fun MainScreen(navController: NavHostController) {
    val recommendedUsers = listOf(
        DummyUser("추천1", "a01062943361@gmail.com"),
        DummyUser("추천2", "a01062943361@gmail.com"),
        DummyUser("추천3", "a01062943361@gmail.com"),
        DummyUser("추천4", "a01062943361@gmail.com"),
        DummyUser("추천5", "a01062943361@gmail.com")
    )

    var selectedRequest by remember { mutableStateOf<RequestEntry?>(null) }
    var showConfirmation by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("오늘의 추천", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(recommendedUsers) { user ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_1),
                        contentDescription = "추천 카드",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(user.name, fontSize = 20.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(onClick = {
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            val senderUid = currentUser?.uid ?: ""
                            val senderEmail = currentUser?.email ?: ""
                            // 실제 대상의 UID로 교체하세요.
                            val targetUid = "TARGET_UID"
                            val targetEmail = "a01062943361@gmail.com"
                            val targetName = "특정 사용자"
                            selectedRequest = RequestEntry(senderUid, senderEmail, targetUid, targetEmail, targetName)
                            showConfirmation = true
                        }) {
                            Text("대화 요청")
                        }
                    }
                }
            }
        }
    }
    if (showConfirmation && selectedRequest != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("대화 요청을 보내시겠습니까?", fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(onClick = {
                        DummyRequestData.sentRequests.add(selectedRequest!!)
                        DummyRequestData.receivedRequests.add(selectedRequest!!)
                        showConfirmation = false
                        selectedRequest = null
                    }) {
                        Text("전송")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = {
                        showConfirmation = false
                        selectedRequest = null
                    }) {
                        Text("취소")
                    }
                }
            }
        }
    }
}
