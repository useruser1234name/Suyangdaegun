package com.ryh.suyangdaegun.screen

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.ryh.suyangdaegun.R
import com.ryh.suyangdaegun.model.ChatMessage
import com.ryh.suyangdaegun.model.ChatViewModel
import com.ryh.suyangdaegun.model.ChatViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun ChattingScreen(navController: NavHostController, viewModel: ChatViewModel) {
    val messages by viewModel.messages.collectAsState()
    var input by remember { mutableStateOf("") }
    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var showDialog by remember { mutableStateOf(false) } // GPT 다이얼로그 상태 관리
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var lastReadIndex by remember { mutableStateOf(-1) }
    val gptSuggestions by viewModel.gptSuggestions.collectAsState(emptyList()) // ✅ GPT 추천 메시지 리스트
    val participantName by viewModel.participantName.collectAsState() // 상대방 이름 가져오기
    val fontSize by viewModel.fontSize.collectAsState() // 🔥 저장된 글씨 크기 가져오기
    var showSlider by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableStateOf(fontSize) } // ✅ 저장된 값 실시간 반영

    LaunchedEffect(Unit) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collectLatest { visibleItems ->
                if (visibleItems.isNotEmpty()) {
                    val lastVisibleIndex = visibleItems.last().index
                    if (lastVisibleIndex > lastReadIndex) {
                        lastReadIndex = lastVisibleIndex
                        viewModel.markMessagesAsRead()
                    }
                }
            }
    }

    //  새로운 메시지가 추가될 때 자동으로 맨 아래로 스크롤
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //  ✅ 채팅방 헤더 (뒤로 가기 | 상대방 이름 | 글자 크기 조절 버튼)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.LightGray)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🔙 뒤로 가기 버튼
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "뒤로가기"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 📌 채팅 상대방 이름
            Text(
                text = participantName,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // 🔠 글자 크기 조절 버튼
            IconButton(
                onClick = { showSlider = !showSlider }, // 🔹 버튼 누를 때마다 슬라이더 보이기/숨기기
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_setting), // 글씨 크기 아이콘 사용
                    contentDescription = "글자 크기 조절 세팅 아이콘"
                )
            }
        }

        // 🔹 글자 크기 조절 슬라이더 (버튼을 눌렀을 때만 표시)
        // 🔹 글자 크기 조절 슬라이더 (실시간 반영)
        if (showSlider) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("글씨 크기", fontSize = 18.sp)

                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it
                        viewModel.setFontSize(it)// 🔥 실시간 반영
                    },
                    valueRange = 12f..40f // 🔹 글씨 크기 범위
                )

                Text("${sliderValue.toInt()} pt", fontSize = 14.sp)
            }
        }


        Column(
            modifier = Modifier
                .weight(0.84f)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState
            ) {
                var lastDate: String? = null

                itemsIndexed(messages) { index, message ->
                    val messageDate =
                        DateFormat.format("yyyy년 MM월 dd일", Date(message.timestamp)).toString()

                    //  날짜가 바뀌면 구분선 추가
                    if (lastDate != messageDate) {
                        lastDate = messageDate
                        DateSeparator(messageDate)
                    }

                    MessageBubble(
                        message = message,
                        isMine = message.senderId == currentUserUid,
                        isUnread = index == messages.lastIndex, //  마지막 메시지인지 확인
                        fontSize = fontSize // ✅ 글씨 크기 적용
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF7F7F7), shape = RoundedCornerShape(24.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
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

            // 메시지 전송 버튼 (사라진 아이콘 추가)
            IconButton(
                onClick = {
                    if (input.isNotBlank()) {
                        viewModel.sendMessage(input)
                        input = ""
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "전송", tint = Color.Black)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 메시지 전송 버튼
            IconButton(
                onClick = {
                    if (messages.isNotEmpty()) {
                        // 🔹 최근 10개의 대화 가져오기
                        val recentMessages = messages.takeLast(10).map {
                            mapOf(
                                "role" to if (it.senderId == currentUserUid) "user" else "assistant",
                                "content" to it.message
                            )
                        }

                        //  GPT에게 전달할 프롬프트 추가 (시스템 역할 포함)
                        val prompt = listOf(
                            mapOf(
                                "role" to "system",
                                "content" to """
                                    
너는 친절하고 똑똑한 대화 도우미야.  
사용자의 대화를 분석하여 상대방과 자연스럽게 이어질 수 있는 3개의 선택지를 추천해줘.  

---
💬 **GPT 추천 답변 요청**
사용자의 대화를 기반으로 **항상 3개의 추천 답변을 제공**해주세요.  
각 답변은 반드시 **1. / 2. / 3.** 형식으로 시작해야 합니다.

✅ **형식 예시**
1. "네, 저도 그렇게 생각해요! 혹시 더 자세히 이야기 나눠볼까요?"
2. "재밌는 이야기네요! 혹시 최근에 관심 있는 주제 있으세요?"
3. "그렇군요. 혹시 좋아하는 영화나 음악이 있나요?"

📌 **반드시 3개의 답변을 제공하고, 번호를 포함해야 합니다.**
📌 **불필요한 설명 없이 바로 답변을 제공합니다.**

### 🔹 **규칙**
- 사용자는 `user`, 상대방은 `assistant`라고 표시되어 있어.
- **대화 흐름을 이해하고**, 문맥에 맞게 자연스러운 답변을 추천해야 해.
- 사용자가 한 말과 상대방이 한 말을 **구분해서 분석**한 후, 적절한 응답을 제안해야 해.
- 대화가 질문이라면 **적절한 답변을 추천**하고, 가벼운 일상 대화라면 **자연스럽게 이어질 수 있도록 답변을 만들어줘.**
- **단답형 답변을 피하고**, 상대방의 감정과 맥락을 고려해서 대화의 흐름을 원활하게 유지해야 해.
- **만약 대화가 끊기거나 애매하면**, 자연스럽게 새로운 주제로 이어갈 수 있는 질문을 포함해줘.  
  (예: "요즘 어떤 거에 관심 있어?" "밥 먹었어요?" "최근에 본 영화 중에 재밌던 거 있어?" 등)

---

### 🔹 **예제 (대화 흐름 분석)**
#### 📌 **예제 1 (자연스러운 흐름 유지)**

👉 **(자연스러운 추천 예시)**
1. "아마도 친구들이랑 영화 보러 갈 것 같아! 너는?"
2. "집에서 푹 쉬면서 책 읽으려고 해. 넌 특별한 계획 있어?"
3. "아직 결정 못 했어. 좋은 추천 있으면 알려줘!"

---

#### 📌 **예제 2 (대화가 끊길 때)**
👉 **(대화가 끊기면 자연스럽게 주제 전환)**
1. "그래도 가끔은 쉬는 시간도 필요하지 않아? 요즘은 어떻게 스트레스 풀어?"
2. "그럼 더 바빠지기 전에 맛있는 거라도 먹으러 가야겠네! 요즘 뭐 자주 먹어?"
3. "그럼 주말에는 좀 쉬는 시간이 있으려나? 어떤 계획 있어?"

---

### 🔹 **대화 추천 포맷**
1. 상대방의 말과 자연스럽게 이어지는 문장을 만들어.
2. 너무 짧거나 단답형이 아닌, **자연스럽고 부드러운 문장**을 사용해.
3. 대화가 끊길 것 같으면 **새로운 주제 전환 질문을 포함**해줘.
4. 가벼운 일상 대화라면 **친근한 톤**으로 대답해줘.

---

### 🔹 **사용자의 최근 대화**
""${'"'}

                    """.trimIndent()
                            )
                        ) + recentMessages


                        // GPT 추천 요청
                        viewModel.requestGptSuggestions(prompt)
                        showDialog = true
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bbo),
                    contentDescription = "GPT 대화 추천"
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            val recentMessages = messages.takeLast(10).map {
                                mapOf(
                                    "role" to if (it.senderId == FirebaseAuth.getInstance().currentUser?.uid) "user" else "assistant",
                                    "content" to it.message
                                )
                            }
                            viewModel.requestGptSuggestions(recentMessages)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6))
                    ) {
                        Text(text = "다시 추천 받기", color = Color.White)
                    }

                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(text = "닫기", color = Color.White)
                    }
                }
            },
            title = {
                Text(
                    text = "💬 GPT 추천 답변",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (gptSuggestions.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        Text(
                            text = "추천 답변을 가져오는 중...",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(gptSuggestions) { _, suggestion ->
                                val formattedSuggestion = suggestion.replace(Regex("^\\d+\\.\\s*"), "") // ✅ 번호 제거

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            input = formattedSuggestion
                                            showDialog = false
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                                ) {
                                    Text(
                                        text = suggestion,
                                        fontSize = 16.sp,
                                        color = Color.Black,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White // 다이얼로그 배경 색상
        )
    }
}

//  메시지 UI
@Composable
fun MessageBubble(message: ChatMessage, isMine: Boolean, isUnread: Boolean, fontSize: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        if (isMine) {
            //  내 메시지: [ 안읽음(1) ] [ 시간 ] [ 말풍선 ]
            //현재 안 읽음은 상대방이 메시지를 보내야만 없어짐
            //fcm토큰을 받아 개인에게 할당해야 하지만 비용 문제로 제고
            //알림서비스도 fcm토큰 할당 필요
            //fcm토큰 할당 시 좀 더 자연스러운 대화 가능할 것으로 사료됨
            //isMine을 이용하여 상대방과 나의 채팅을 구분
            Row(verticalAlignment = Alignment.Bottom) {
                //  안읽음(1)은 왼쪽 끝 바깥쪽으로 이동
                if (isUnread) {
                    Text(
                        text = if (!message.isRead) "1" else "",
                        fontSize = 12.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }

                // 전송 시간 표시
                Text(
                    text = DateFormat.format("HH:mm", Date(message.timestamp)).toString(),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 4.dp)
                )

                // 말풍선 (내 메시지)
                ChatBubble(message.message, isMine, fontSize)
            }
        } else {
            // 상대 메시지: [ 말풍선 ] [ 시간 ] [ 안읽음(1) ]
            Row(verticalAlignment = Alignment.Bottom) {
                // 말풍선 (상대방 메시지)
                ChatBubble(message.message, isMine, fontSize)

                // 시간 표시
                Text(
                    text = DateFormat.format("HH:mm", Date(message.timestamp)).toString(),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )

                // 안읽음(1)은 오른쪽 끝 바깥쪽으로 이동
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


//  날짜 구분선 UI
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


// 말풍선 UI 컴포넌트
@Composable
fun ChatBubble(text: String, isMine: Boolean, fontSize: Float) {
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
                fontSize = fontSize.sp,
                color = Color.Black
            )
        }
    }
}


