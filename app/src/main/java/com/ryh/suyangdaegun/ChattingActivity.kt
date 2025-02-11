package com.ryh.suyangdaegun

import android.text.format.DateFormat
import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun ChattingScreen(navController: NavHostController, viewModel: ChatViewModel) {
    val messages by viewModel.messages.collectAsState()
    var input by remember { mutableStateOf("") }
    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // ✅ 새로운 메시지가 추가될 때 자동으로 맨 아래로 스크롤
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // ✅ 채팅방 헤더 추가
        Box(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .background(color = Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "채팅방", fontSize = 20.sp)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                state = listState
            ) {
                var lastDate: String? = null

                itemsIndexed(messages) { index, message ->
                    val messageDate = DateFormat.format("yyyy년 MM월 dd일", Date(message.timestamp)).toString()

                    // ✅ 날짜가 바뀌면 구분선 추가
                    if (lastDate != messageDate) {
                        lastDate = messageDate
                        DateSeparator(messageDate)
                    }

                    MessageBubble(
                        message = message,
                        isMine = message.senderId == currentUserUid,
                        isUnread = index == messages.lastIndex // ✅ 마지막 메시지인지 확인
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF7F7F7), shape = RoundedCornerShape(24.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, shape = RoundedCornerShape(24.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    placeholder = { Text("메시지를 입력하세요") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            viewModel.sendMessage(input)
                            input = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF8B5CF6), shape = RoundedCornerShape(50)) // 보라색 버튼
                ) {
                    Icon(Icons.Default.Send, contentDescription = "전송", tint = Color.White)
                }
            }

        }
    }
}

// ✅ 날짜 구분선 UI
@Composable
fun DateSeparator(date: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
        ) {
            Text(
                text = date,
                modifier = Modifier.padding(8.dp),
                color = Color.DarkGray,
                fontSize = 14.sp
            )
        }
    }
}



// ✅ 메시지 UI
@Composable
fun MessageBubble(message: ChatMessage, isMine: Boolean, isUnread: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        if (isMine) {
            // ✅ 내 메시지: [ 안읽음(1) ] [ 시간 ] [ 말풍선 ]
            Row(verticalAlignment = Alignment.Bottom) {
                // ✅ 안읽음(1)은 왼쪽 끝 바깥쪽으로 이동
                if (isUnread) {
                    Text(
                        "1",
                        fontSize = 12.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }

                // 시간 표시
                Text(
                    text = DateFormat.format("HH:mm", Date(message.timestamp)).toString(),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 4.dp)
                )

                // 말풍선 (내 메시지)
                ChatBubble(message.message, isMine)
            }
        } else {
            // ✅ 상대 메시지: [ 말풍선 ] [ 시간 ] [ 안읽음(1) ]
            Row(verticalAlignment = Alignment.Bottom) {
                // 말풍선 (상대방 메시지)
                ChatBubble(message.message, isMine)

                // 시간 표시
                Text(
                    text = DateFormat.format("HH:mm", Date(message.timestamp)).toString(),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )

                // ✅ 안읽음(1)은 오른쪽 끝 바깥쪽으로 이동
                if (isUnread) {
                    Text(
                        "1",
                        fontSize = 12.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

// ✅ 말풍선 UI 컴포넌트
@Composable
fun ChatBubble(text: String, isMine: Boolean) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMine) Color(0xFFFFEB3B) else Color.LightGray
        ),
        modifier = Modifier
            .padding(4.dp)
            .widthIn(max = 300.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = text,
                fontSize = 24.sp,
                color = Color.Black
            )
        }
    }
}


