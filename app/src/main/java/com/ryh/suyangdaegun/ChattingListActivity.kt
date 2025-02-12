package com.ryh.suyangdaegun

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.DividerDefaults.color
import androidx.compose.material3.SnackbarDefaults.color
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun ChatListScreen(navController: NavHostController, viewModel: ChatListViewModel = viewModel()) {
    val chatRooms by viewModel.chatRooms.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text(
            "채팅 목록", style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.height(50.dp),
            fontSize = 25.sp
        )
        Divider(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(chatRooms) { chatRoom ->
                ChatRoomItem(chatRoom, navController)
            }
        }
    }
}

// ✅ 채팅방 리스트 아이템 UI
@Composable
fun ChatRoomItem(chatRoom: ChatRoomItem, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("chatting/${chatRoom.chatRoomId}") },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "참여자: ${chatRoom.participantName}",
                style = MaterialTheme.typography.bodyLarge
            ) // ✅ 변수명 수정
            Text("마지막 메시지: ${chatRoom.lastMessage}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
