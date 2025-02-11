package com.ryh.suyangdaegun

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val recommendedUsers = listOf(
        "당신의 부드러운 이목구비와 상대의 강한 인상이 조화를 이루어, 서로의 매력을 더욱 극대화할 수 있는 운명적인 만남입니다. 이런 조합은 안정적이면서도 설레는 감정을 동시에 불러일으킬 것입니다.",
        "서로의 얼굴형과 이목구비가 마치 거울을 보는 듯한 완벽한 조화를 이루며, 이는 단순한 외형적 유사성을 넘어 정신적인 교감을 깊이 형성할 수 있는 궁합을 의미합니다. 자연스럽고 편안한 관계를 기대해보세요.",
        "당신과 이 상대는 사주적으로 강한 '금(金) 오행'을 공유하며, 이는 강한 끌림과 함께 신뢰를 기반으로 한 안정적인 관계를 약속합니다. 또한 양(陽) 기운이 지배하는 조합이므로, 함께 있을 때 에너지가 넘치고 활력이 샘솟는 만남이 될 것입니다.",
        "처음엔 다소 어울리지 않을 것 같지만, 서로가 부족한 부분을 보완해주는 황금 비율의 조합입니다. 얼굴형이 다르더라도 공통된 사주적 특성이 있어 서로를 끌어당기는 강한 인연이 형성될 것입니다. 진정한 소울메이트를 찾았다면 바로 이런 느낌일 것입니다.",
        "이마의 곡선과 입술의 라인까지 유사한 당신과 상대방은 마치 퍼즐 조각처럼 완벽히 맞아떨어집니다. 이런 조합은 신뢰와 애정을 바탕으로 한 깊은 관계로 이어지며, 단순한 만남을 넘어 평생의 인연으로 발전할 가능성이 큽니다.",
        "첫 만남부터 따뜻한 온기가 느껴지는 조합입니다. 서로의 눈빛 속에서 공감을 읽을 수 있으며, 대화가 끊기지 않는 놀라운 경험을 하게 될 것입니다. 운명적인 만남이란 이런 것이 아닐까요?",
        "서로 다른 듯하면서도 묘하게 닮아있는 얼굴형 덕분에, 함께 있을 때 자연스럽게 안정감을 느낄 수 있는 관계입니다. 이는 단순한 외적 궁합을 넘어 심리적인 교감까지 이어질 가능성이 높은 인연입니다.",
        "반대의 매력이 극대화되는 조합입니다. 당신의 부드러움이 상대방의 강인함을 더욱 돋보이게 만들며, 상대방 역시 당신을 더욱 보호하고 싶어질 것입니다. 서로가 없으면 허전함을 느낄 만큼 강렬한 유대감을 형성할 것입니다.",
        "단순한 외형적 궁합이 아닌, 함께 있을 때 편안함과 설렘을 동시에 느낄 수 있는 관계입니다. 당신과 상대는 서로에게 치유와 긍정적인 에너지를 줄 수 있는 희귀한 조합을 이루고 있습니다.",
        "당신의 직관력과 상대의 현실적인 성향이 조화를 이루며, 단순한 만남을 넘어 서로를 성장시키는 관계로 발전할 가능성이 큽니다. 함께라면 어떤 어려움도 극복할 수 있는 강한 인연입니다."
    )


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("홈", modifier = Modifier.padding(bottom = 16.dp))

        Divider(modifier = Modifier.fillMaxWidth())

        LazyColumn {
            items(recommendedUsers.take(5)) { title ->
                MatchUserCard(title, viewModel, rootNavController)
            }
        }
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D3A31))
        ) {
            Text("운명적인 친구 더 찾아보기", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White)
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
            .size(height = 290.dp, width = 370.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
            confirmButton = {
                Button(onClick = {
                    val targetEmail = "a01062946631@gmail.com"
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
                }) { Text("YES") }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) { Text("NO") }
            }
        )
    }
}
