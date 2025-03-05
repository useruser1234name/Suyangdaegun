package com.ryh.suyangdaegun.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.auth.FirebaseAuth
import com.ryh.suyangdaegun.R
import com.ryh.suyangdaegun.model.MatchRequest
import com.ryh.suyangdaegun.model.MatchingViewModel
import com.ryh.suyangdaegun.model.UserHelper
import com.ryh.suyangdaegun.navi.AppNavigatorMain
import kotlinx.coroutines.launch

class MatchingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 메인 네비게이션 실행
        setContent { AppNavigatorMain() }
    }
}

@OptIn(ExperimentalPagerApi::class)
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

    val tabTitles = listOf("📩 받은 요청", "📤 보낸 요청")
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding()
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
            Image(
                painter = painterResource(R.drawable.ic_alarm),
                contentDescription = "알람 아이콘",
                modifier = Modifier.size(28.dp)
            )
        }
        Divider(modifier = Modifier.fillMaxWidth())

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            count = tabTitles.size,
            state = pagerState,
            modifier = Modifier.weight(1f) // 👈 전체 화면을 차지하도록 설정
        ) { page ->
            when (page) {
                0 -> ReceivedRequestsScreen(receivedRequests, viewModel, navController)
                1 -> SentRequestsScreen(sentRequests, viewModel)
            }
        }
    }
}

@Composable
fun ReceivedRequestsScreen(
    receivedRequests: List<MatchRequest>,
    viewModel: MatchingViewModel,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize() // 👈 화면 전체를 차지하도록 설정
    ) {
        items(receivedRequests) { request ->
            RequestCard(request, true, viewModel, navController)
        }
    }
}

@Composable
fun SentRequestsScreen(
    sentRequests: List<MatchRequest>,
    viewModel: MatchingViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize() // 👈 화면 전체를 차지하도록 설정
    ) {
        items(sentRequests) { request ->
            SentRequestCard(request, viewModel)
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
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bbo), // ❤️ 아이콘 추가
                        contentDescription = "매칭 아이콘",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "💌 매칭 요청",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "**${senderNickname}** 님이 매칭을 요청하셨습니다!",
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Image(
                        painter = painterResource(id = R.drawable.ic_heart), // 하트 이미지 추가
                        contentDescription = "Heart Icon",
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "수락하면 채팅방이 생성됩니다.",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // ✅ 수락 버튼 - 초록색
                    Button(
                        onClick = {
                            viewModel.approveMatchRequest(request) { chatRoomId ->
                                navController.navigate("chatting/$chatRoomId") // 채팅방 이동
                            }
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32), // 초록색
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "✅ 수락", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    // ✅ 거절 버튼 - 회색
                    Button(
                        onClick = {
                            viewModel.declineMatchRequest(request)
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF757575), // 회색
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "❌ 거절", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            shape = RoundedCornerShape(16.dp), // 다이얼로그 모서리 둥글게
            containerColor = Color.White // 다이얼로그 배경 색상
        )
    }

}

@Composable  //내가 보내놓고 마음이 변해서 취소하고 싶을때 사용
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
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bbo),
                        contentDescription = "경고 아이콘",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🚨 요청 취소",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "**${receiverNickname}** 님에게 보낸 매칭 요청을 취소하시겠습니까?",
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Image(
                        painter = painterResource(id = R.drawable.ic_bbo), // ⚠️ 아이콘 추가
                        contentDescription = "경고 아이콘",
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "취소 후 되돌릴 수 없습니다.",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // ✅ 요청 취소 버튼 - 빨간색
                    Button(
                        onClick = {
                            viewModel.cancelMatchRequest(request)
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F), // 빨간색
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "🛑 취소", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    // ✅ 유지 버튼 - 회색
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF757575), // 회색
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "🔄 유지", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            shape = RoundedCornerShape(16.dp), // 다이얼로그 모서리 둥글게
            containerColor = Color.White // 다이얼로그 배경 색상
        )
    }

}
