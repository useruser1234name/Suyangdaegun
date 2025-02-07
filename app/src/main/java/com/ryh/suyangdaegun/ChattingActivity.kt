package com.ryh.suyangdaegun

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ChattingScreen(navController: NavHostController, viewModel: ChatViewModel) {
    val messages by viewModel.messages.collectAsState() // ✅ 메시지 실시간 반영

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("채팅 화면", style = MaterialTheme.typography.headlineMedium)

        // ✅ 채팅 메시지 리스트 표시
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages) { message ->
                Text(
                    text = "${message.sender}: ${message.content}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // ✅ 메시지 입력 UI
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            var input by remember { mutableStateOf("") }
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("메시지 입력") }
            )
            Button(onClick = {
                if (input.isNotBlank()) {
                    viewModel.sendMessage(input) // ✅ Firebase에 메시지 전송
                    input = ""
                }
            }) {
                Text("전송")
            }
        }
    }
}
