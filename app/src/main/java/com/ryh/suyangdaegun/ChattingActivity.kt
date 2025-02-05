package com.ryh.suyangdaegun

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val messages by viewModel.messages.collectAsState()
    var input by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // 메시지 목록
        LazyColumn(
            modifier = Modifier.weight(1f).padding(8.dp),
            reverseLayout = true
        ) {
            items(messages) { message ->
                MessageBubble(
                    content = message.content,
                    isUserMessage = message.sender == viewModel.currentUserUID,
                    isRead = message.isRead
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // 메시지 입력 필드 및 전송 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("메시지 입력") },
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    if (input.isNotBlank()) {
                        viewModel.sendMessage(input)
                        input = ""
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("전송")
            }
        }
    }
}

@Composable
fun MessageBubble(content: String, isUserMessage: Boolean, isRead: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (isUserMessage) Color(0xFFE1F5FE) else Color(0xFFFFF9C4),
                    shape = CircleShape
                )
                .padding(12.dp)
        ) {
            Text(content, fontSize = 16.sp)
        }

        if (!isUserMessage && !isRead) {
            Spacer(modifier = Modifier.width(8.dp))
            UnreadCountIndicator(1)
        }
    }
}

@Composable
fun UnreadCountIndicator(unreadCount: Int) {
    if (unreadCount > 0) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(24.dp)
                .background(Color.Red, shape = CircleShape)
        ) {
            Text(
                text = "$unreadCount",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

