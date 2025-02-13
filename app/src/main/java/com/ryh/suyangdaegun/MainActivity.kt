package com.ryh.suyangdaegun

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 메인 네비게이션 실행
        setContent { AppNavigatorMain() }
    }
}

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val viewModel: MatchingViewModel = viewModel()
    val visible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible.value = true
    }

    val recommendedUsers = listOf(
        "당신의 부드러운 이목구비와 상대의 강렬한 인상이 만나 폭발적인 케미를 일으킬 운명의 커플입니다.",
        "거울을 보는 듯 놀라운 싱크로율의 얼굴형과 이목구비는 당신들이 태생부터 운명적으로 연결된 소울메이트임을 보여줍니다.",
        "당신과 상대방의 '금(金) 오행'이 만나 폭발적인 스파크가 일어날 천생연분의 커플입니다.",
        "정반대의 매력이 만나 그 어떤 배우 커플보다도 완벽한 비주얼 시너지를 만들어낼 운명의 한 쌍입니다.",
        "이마부터 입술까지 완벽한 황금비율로 조화를 이루는 당신들은 하늘이 점지한 최고의 커플입니다.",
        "첫 만남부터 터지는 케미스트리로 주변의 모든 시선을 사로잡을 운명의 커플입니다.",
        "겉모습은 달라도 묘하게 일치하는 당신들의 얼굴형은 천생연분임을 증명하는 가장 확실한 증거입니다.",
        "극과 극이 만나 폭발적인 chemistry를 만들어내는 당신들의 조합은 그 자체로 완벽한 걸작입니다.",
        "얼굴의 모든 요소가 만드는 완벽한 조화는 마치 운명이 빚어낸 예술품 같은 커플입니다.",
        "서로의 눈빛이 마주치는 순간, 르네상스 시대의 걸작을 보는 듯한 감동을 선사하는 운명의 커플입니다."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "홈",
                fontSize = 25.sp,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("${UserState.nickname}님 오늘의 추천 상대입니다", fontSize = 20.sp)
            // -> 현재 가입을 해야만 닉네임 뜨는데 수정 가능
            }

            Spacer(modifier = Modifier.width(30.dp))
        }

        Divider(modifier = Modifier.fillMaxWidth())

        // 스크롤 영역에 weight(1f) 추가하여 남은 공간만 사용하도록 함
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            recommendedUsers.take(5).forEachIndexed { index, title ->
                AnimatedVisibility(
                    visible = visible.value,
                    enter = slideInVertically(
                        initialOffsetY = { it * 2 },
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = index * 200 // 각 아이템마다 약간의 딜레이를 줘서 순차적으로 나타나게 함
                        )
                    ) + fadeIn(
                        initialAlpha = 0f,
                        animationSpec = tween(durationMillis = 300)
                    )
                ) {
                    MatchUserCard(title, viewModel, rootNavController)
                }
            }
        }
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D3A31))
        ) {
            Text(
                "운명적인 친구 더 찾아보기",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White
            )
        }

    }
}


@Composable
fun MatchUserCard(title: String, viewModel: MatchingViewModel, navController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { showDialog = true }
            .size(height = 130.dp, width = 370.dp),

        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)  // 연한 실버
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("매칭 요청") },
            text = { Text("이 사용자와 매칭 요청을 보낼까요?") },
            confirmButton = { //  YES 버튼을 먼저 배치
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End //  오른쪽 정렬
                ) {
                    Button(
                        onClick = {
                            val targetEmail = "ddong0273@gmail.com"  //현재 타겟이메일 설정 이유 == firestore 과부화 방지
                            //추후 여러 유저 대상으로 매칭 확장 가능 -> recommendeduser와 연결해서 매칭 가능
                            // 서버 연결 필요
                            viewModel.getUserUidByEmail(targetEmail) { targetUid ->
                                if (targetUid != null) {
                                    viewModel.sendMatchRequestToFirestore(targetUid) { success ->
                                        if (success) Log.d("Matching", "매칭 요청 성공!")
                                        showDialog = false
                                    }
                                } else {
                                    Log.e("Matching", "사용자를 찾을 수 없습니다.")
                                //firestore users에 db 없는 경우 -> 회원탈퇴
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black, //  버튼 배경 검은색
                            contentColor = Color.White    //  버튼 글자 흰색
                        )
                    ) { Text("YES") }

                    Spacer(modifier = Modifier.width(8.dp)) //  버튼 사이 간격 추가

                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black, //  버튼 배경 검은색
                            contentColor = Color.White    //  버튼 글자 흰색
                        )
                    ) { Text("NO") }
                }
            }
        )
    }
}

