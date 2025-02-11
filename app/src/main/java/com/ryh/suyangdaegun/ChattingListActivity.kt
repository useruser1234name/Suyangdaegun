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

@Composable
fun ChatListScreen(navController: NavHostController, viewModel: ChatListViewModel = viewModel()) {
    val chatRooms by viewModel.chatRooms.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("채팅 목록", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(chatRooms) { chatRoom ->
                ChatRoomItem(chatRoom, navController)
            }
        }
    }
}

// 🔹 채팅방 리스트 아이템 UI
@Composable
fun ChatRoomItem(chatRoom: ChatRoom, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("chatting/${chatRoom.chatRoomId}") }, // ✅ 채팅방으로 이동
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("채팅방 ID: ${chatRoom.chatRoomId}")
            Text("참여자: ${chatRoom.participants.joinToString()}")
        }
    }
}
