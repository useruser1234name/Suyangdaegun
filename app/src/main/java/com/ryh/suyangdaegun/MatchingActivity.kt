package com.ryh.suyangdaegun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

class MatchingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 메인 네비게이션 실행
        setContent { AppNavigatorMain() }
    }
}

@Composable
fun MatchingScreen(navController: NavHostController) {
    val viewModel: MatchingViewModel = viewModel()
    val currentUser = FirebaseAuth.getInstance().currentUser

    var receivedRequests by remember { mutableStateOf(emptyList<MatchRequest>()) }
    var sentRequests by remember { mutableStateOf(emptyList<MatchRequest>()) }

    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            viewModel.loadReceivedRequests(user.uid) { requests -> receivedRequests = requests }
            viewModel.loadSentRequests(user.uid) { requests -> sentRequests = requests }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "매칭 요청", style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.height(50.dp),
            fontSize = 25.sp
        )

        Divider(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(8.dp))

        Column() {
            Text("📩 받은 요청", style = MaterialTheme.typography.titleMedium)
            LazyColumn(modifier = Modifier.weight(0.5f)) {
                items(receivedRequests) { request ->
                    RequestCard(request, isReceived = true, viewModel, navController)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("📤 보낸 요청", style = MaterialTheme.typography.titleMedium)
            LazyColumn(modifier = Modifier.weight(0.5f)) {
                items(sentRequests) { request ->
                    SentRequestCard(request, viewModel)
                }
            }
        }
    }
}


@Composable
fun RequestCard(
    request: MatchRequest,
    isReceived: Boolean,
    viewModel: MatchingViewModel,
    navController: NavHostController
) {
    var showDialog by remember { mutableStateOf(false) }
    var senderNickname by remember { mutableStateOf("로딩 중...") }
    var myNickname by remember { mutableStateOf("로딩 중...") }

    LaunchedEffect(request.senderUid) {
        UserHelper.getParticipantName(request.senderUid) { nickname ->
            senderNickname = nickname
        }
    }

    LaunchedEffect(Unit) {
        UserHelper.getCurrentUserNickname { nickname ->
            myNickname = nickname
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showDialog = true },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(if (isReceived) "보낸 사람: $senderNickname" else "받은 사람: $myNickname")
            Text("상태: ${request.status}")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("매칭 요청") },
            text = { Text("$senderNickname 님의 요청을 수락하시겠습니까?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.approveMatchRequest(request) { chatRoomId ->
                        navController.navigate("chatting/$chatRoomId") // ✅ 수락 후 자동 이동
                    }
                    showDialog = false
                }) { Text("수락") }
            },
            dismissButton = {
                Button(onClick = {
                    viewModel.declineMatchRequest(request)
                    showDialog = false
                }) { Text("거절") }
            }
        )
    }
}

@Composable
fun SentRequestCard(request: MatchRequest, viewModel: MatchingViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var receiverNickname by remember { mutableStateOf("로딩 중...") }

    LaunchedEffect(request.receiverUid) {
        UserHelper.getParticipantName(request.receiverUid) { nickname ->
            receiverNickname = nickname
        }
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showDialog = true },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("받는 사람: $receiverNickname")
            Text("상태: ${request.status}")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("매칭 요청 관리") },
            text = { Text("이 요청을 취소하시겠습니까?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.cancelMatchRequest(request)
                    showDialog = false
                }) { Text("요청 취소") }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) { Text("유지") }
            }
        )
    }
}
