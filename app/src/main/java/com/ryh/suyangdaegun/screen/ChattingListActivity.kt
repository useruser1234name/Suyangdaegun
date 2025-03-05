package com.ryh.suyangdaegun.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ryh.suyangdaegun.R
import com.ryh.suyangdaegun.model.ChatListViewModel
import com.ryh.suyangdaegun.model.ChatRoomItem

@Composable
fun ChatListScreen(navController: NavHostController, viewModel: ChatListViewModel = viewModel()) {
    val chatRooms by viewModel.chatRooms.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "채팅",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.height(50.dp),
                fontSize = 25.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_ring),
                    contentDescription = "알람 아이콘",
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Image(
                    painter = painterResource(R.drawable.ic_alarm),
                    contentDescription = "상담사 연결 아이콘",
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Image(
                    painter = painterResource(R.drawable.ic_setting),
                    contentDescription = "세팅 아이콘",
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Divider(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(8.dp))

        if (chatRooms.isEmpty()) {
            // ✅ 채팅방이 없을 때 안내 메시지를 가운데 정렬하여 표시
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center // 📌 화면 중앙 정렬
            ) {
                Text(
                    text = "서로 편지를 주고 받았을 때 대화가 시작돼요",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn {
                items(chatRooms) { chatRoom ->
                    ChatRoomItem(chatRoom, navController)
                }
            }
        }
    }
}

//  채팅방 리스트 아이템 UI
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
            ) //  변수명 수정
            Text("마지막 메시지: ${chatRoom.lastMessage}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
