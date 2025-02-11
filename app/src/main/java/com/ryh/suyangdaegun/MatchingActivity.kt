package com.ryh.suyangdaegun

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("매칭 요청", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))

        Text("📩 받은 요청", style = MaterialTheme.typography.titleMedium)
        LazyColumn(modifier = Modifier.height(300.dp)) {
            items(receivedRequests) { request ->
                RequestCard(request, isReceived = true, viewModel, navController)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("📤 보낸 요청", style = MaterialTheme.typography.titleMedium)
        LazyColumn(modifier = Modifier.height(300.dp)) {
            items(sentRequests) { request ->
                RequestCard(request, isReceived = false, viewModel, navController)
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showDialog = true },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(if (isReceived) "보낸 사람: ${request.senderEmail}" else "받은 사람: ${request.receiverEmail}")
            Text("상태: ${request.status}")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("매칭 요청") },
            text = { Text("${request.senderEmail}님의 요청을 수락하시겠습니까?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.approveMatchRequest(request) { chatRoomId ->
                        navController.navigate("chatting/$chatRoomId")
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
