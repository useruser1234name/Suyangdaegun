package com.ryh.suyangdaegun

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ✅ `dumChatMessage`를 제대로 사용하도록 수정
data class dumChatMessage(val text: String, val isMine: Boolean)

@Composable
fun MyPageScreen(navController: NavHostController) {
    var message by remember { mutableStateOf("") }
    var chatLog by remember { mutableStateOf(listOf<dumChatMessage>()) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("채팅 화면", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom
        ) {
            chatLog.forEach { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isMine) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (msg.isMine) Color(0xFFDCF8C6) else Color(0xFFECECEC)
                        ),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            msg.text,
                            modifier = Modifier.padding(8.dp),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f).padding(8.dp)
            )
            Button(onClick = {
                if (message.isNotBlank()) {
                    chatLog = chatLog + dumChatMessage(message, isMine = true)
                    message = ""

                    coroutineScope.launch {
                        delay(5000) // ⏳ 5초 대기 후 상대방 응답
                        chatLog = chatLog + dumChatMessage("안녕하세요!", isMine = false)
                    }
                }
            }) {
                Text("보내기")
            }
        }
    }
}
