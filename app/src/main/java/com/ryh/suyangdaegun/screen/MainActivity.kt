package com.ryh.suyangdaegun.screen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.ryh.suyangdaegun.R
import com.ryh.suyangdaegun.model.MatchingViewModel
import com.ryh.suyangdaegun.model.UserHelper
import com.ryh.suyangdaegun.navi.AppNavigatorMain

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
    var nickname by remember { mutableStateOf("사용자") } // 기본값 설정


    LaunchedEffect(Unit) {
        visible.value = true
        UserHelper.getCurrentUserNickname { fetchedNickname ->
            nickname = fetchedNickname // 닉네임 업데이트
        }
    }

    val recommendedUsers = listOf(
        "상대방: 도화살(桃花煞), 은광살(銀光煞)\n" +
                "특징: 이성이 끊이지 않는 매력적인 성격과 화려한 외모를 가진 사람입니다. 타인의 시선을 자연스럽게 사로잡으며, 인기와 주목을 받는 삶을 살아갑니다.\n\n" +
                "풀이: 도화살이 강한 상대는 쉽게 사랑에 빠지지만, 금방 식을 수도 있습니다. 은광살은 재물운이 있지만 쉽게 소비하는 경향이 있어, 금전 관리가 필요합니다.\n\n" +
                "궁합 포인트: 당신이 신뢰를 중요시하고, 안정적인 연애를 원한다면 초반에는 상대방과의 관계가 다소 불안정할 수 있습니다. 하지만 서로 배려한다면 뜨거운 로맨스를 즐길 수 있는 최고의 궁합입니다.\n\n" +
                "추가 조언: 상대에게 신뢰를 주는 것이 가장 중요합니다. 가벼운 관계가 아닌 깊이 있는 관계로 발전시키기 위해서는 감정을 천천히 쌓아가세요.",

        "상대방: 살기살(殺氣煞), 권세살(權勢煞)\n" +
                "특징: 강한 카리스마와 리더십을 타고난 사람입니다. 목표를 향해 돌진하는 성향이 강하며, 자신이 원하는 것은 반드시 이루려는 타입입니다.\n\n" +
                "풀이: 권세살이 강한 사람은 사회적으로 성공할 확률이 높지만, 감정 표현이 서툴러 주변 사람들에게 차갑게 보일 수 있습니다. 살기살이 있으면 다소 공격적인 성향이 강할 수 있어, 온화한 사람이 균형을 맞춰줄 필요가 있습니다.\n\n" +
                "궁합 포인트: 당신이 상대방에게 감성적인 안정감을 줄 수 있다면, 서로의 부족한 점을 채워주는 최상의 조합이 될 수 있습니다. 강한 리더십을 가진 상대를 존중하면서도, 부드러운 소통을 이어간다면 관계가 오래 지속될 것입니다.\n\n" +
                "추가 조언: 상대방의 성공 지향적인 성향을 존중하면서도, 감정적으로 소외되지 않도록 배려와 대화가 필요합니다.",

        "상대방: 재물살(財物煞), 고독살(孤獨煞)\n" +
                "특징: 돈을 모으는 능력이 뛰어나지만, 감정적으로는 외로움을 많이 타는 사람입니다. 물질적으로는 풍족할 가능성이 높지만, 인간관계에서는 의외로 소극적일 수 있습니다.\n\n" +
                "풀이: 재물운이 강하기 때문에 사업을 하면 성공할 확률이 높습니다. 하지만 고독살이 있으면 내면적으로 공허함을 느끼거나 깊은 관계를 맺는 것이 어려울 수 있습니다.\n\n" +
                "궁합 포인트: 당신이 따뜻한 감성을 가지고 있고, 대화를 통해 상대를 이해하려는 노력만 있다면, 상대방은 당신에게 점점 더 의지하게 될 것입니다. 함께 있을 때 안정감을 느끼도록 만들어 주세요.\n\n" +
                "추가 조언: 상대가 감정을 쉽게 표현하지 않더라도, 깊은 애정을 가지고 있다는 점을 이해하는 것이 중요합니다.",

        "상대방: 형제살(兄弟煞), 관록살(官祿煞)\n" +
                "특징: 주변 사람들과의 관계를 소중히 여기며, 사교성이 뛰어난 성격을 가지고 있습니다. 하지만 사회적 책임감이 강해 스트레스를 받는 경우도 많습니다.\n\n" +
                "풀이: 형제살이 있는 사람은 친구나 가족을 위해 희생하는 성향이 강합니다. 하지만 관록살이 있으면 직장이나 사회에서 큰 책임을 맡을 가능성이 높으며, 이에 따른 스트레스가 존재합니다.\n\n" +
                "궁합 포인트: 당신이 상대방의 고민을 잘 들어줄 수 있다면, 상대는 당신을 가장 편안한 사람으로 생각하게 될 것입니다. 서로를 이해하고 위로하는 관계가 될 가능성이 높습니다.\n\n" +
                "추가 조언: 상대방이 바쁘고 힘든 시간을 보낼 때, 작은 관심과 배려로 그 마음을 채워주세요.",

        "상대방: 복록살(福祿煞), 식복살(食福煞)\n" +
                "특징: 전반적인 운이 좋은 사람으로, 기본적으로 먹을 복과 생활의 안정성을 타고난 타입입니다. 성실하면서도 타인에게 신뢰를 주는 성격입니다.\n\n" +
                "풀이: 복록살이 있는 사람은 금전적 안정성이 뛰어나며, 평온한 삶을 살아갈 가능성이 큽니다. 또한 식복살 덕분에 먹는 것을 좋아하고, 미식가일 확률이 높습니다.\n\n" +
                "궁합 포인트: 당신이 활발하고 열정적인 성격이라면, 상대의 안정적인 에너지가 편안한 연애를 만들어 줄 것입니다. 서로가 서로에게 평온함과 활력을 줄 수 있는 최고의 조합입니다.\n\n" +
                "추가 조언: 상대와 함께 맛집을 찾아다니며 소소한 행복을 공유하면, 더욱 깊은 관계로 발전할 수 있습니다."
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
                Text("${nickname}님 오늘의 추천 상대입니다", fontSize = 20.sp)
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
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE75480))
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
fun MatchUserCard(
    title: String,
    viewModel: MatchingViewModel,
    navController: NavHostController
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { showDialog = true }
            .size(height = 300.dp, width = 370.dp),

        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)  // 연한 실버
        )
    ) {
        Column(modifier = Modifier
            .fillMaxSize() // 🎯 카드 내부 크기를 100% 채우도록 설정
            .verticalScroll(rememberScrollState())
            .padding(16.dp)) {
            Text(title)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "💌 매칭 요청",
                    fontSize = 22.sp,
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
                    Text(
                        text = "이 사용자와 매칭 요청을 보낼까요?",
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.ic_chat), // ❤️ 아이콘 추가
                        contentDescription = "Matching Icon",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Red
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
                    // ✅ YES 버튼 - 초록색 (성공 느낌 강조)
                    Button(
                        onClick = {
                            val targetEmail = "sts0514974@gmail.com"
                            viewModel.getUserUidByEmail(targetEmail) { targetUid ->
                                if (targetUid != null) {
                                    viewModel.sendMatchRequestToFirestore(targetUid) { success ->
                                        if (success) Log.d("Matching", "매칭 요청 성공!")
                                        showDialog = false
                                    }
                                } else {
                                    Log.e("Matching", "사용자를 찾을 수 없습니다.")
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32), // 초록색
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "YES", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp)) //  버튼 사이 간격 추가

                    // ✅ NO 버튼 - 회색 (덜 강조)
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF757575), // 회색
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "NO", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            shape = RoundedCornerShape(16.dp), // 다이얼로그 모서리 둥글게
            containerColor = Color.White // 다이얼로그 배경 색상
        )
    }

}

