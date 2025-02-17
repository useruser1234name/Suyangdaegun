package com.ryh.suyangdaegun.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
            title = { Text("매칭 요청") },
            text = { Text("$senderNickname 님의 요청을 수락하시겠습니까?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.approveMatchRequest(request) { chatRoomId ->
                            navController.navigate("chatting/$chatRoomId") // 수락 후 자동 이동
                        }

                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) { Text("수락") }
            },
            dismissButton = {
                Button(
                    onClick = {
                        viewModel.declineMatchRequest(request)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) { Text("거절") }
            }
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
            title = { Text("매칭 요청 관리") },
            text = { Text("이 요청을 취소하시겠습니까?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelMatchRequest(request)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) { Text("요청 취소") }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) { Text("유지") }
            }
        )
    }
}
