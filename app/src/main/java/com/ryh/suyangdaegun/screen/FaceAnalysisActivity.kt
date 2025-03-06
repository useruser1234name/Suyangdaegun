package com.ryh.suyangdaegun.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
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
import coil3.compose.rememberAsyncImagePainter
import com.ryh.suyangdaegun.model.RegistrationViewModel

@Composable
fun FaceAnalysisScreen(
    navController: NavHostController,
    viewModel: RegistrationViewModel = viewModel()
) {
    val birthdate = viewModel.getBirthdate()
    val birthtime = viewModel.getBirthtime()
    val profilePictureUrl = viewModel.getProfilePicture()
    val selectedImageUri = profilePictureUrl?.let { Uri.parse(it) }

    var isLoading by remember { mutableStateOf(false) }
    var isAnalysisDone by remember { mutableStateOf(false) }
    var faceResult by remember { mutableStateOf("얼굴 분석 불러오는 중...") }
    var fortuneResult by remember { mutableStateOf("사주 분석 불러오는 중...") }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            kotlinx.coroutines.delay(9000) // 🔥 5초 동안 로딩 후
            isLoading = false
            isAnalysisDone = true

            // 🔹 더미 데이터
            faceResult = """

                - 이마가 넓어 리더십이 강함
                - 눈이 크고 선명하여 감성적이고 창의적임
                - 코가 높아 재물운이 좋음
                - 입꼬리가 올라가 있어 긍정적 성향
            """.trimIndent()

            fortuneResult = """

                - 올해 전반적인 운세는 안정적이나 가을에 변동 가능성이 있음
                - 직장운: 새로운 기회가 찾아올 가능성 높음
                - 연애운: 주변에 좋은 인연이 있을 가능성 큼
                - 건강운: 과로를 피하고 충분한 휴식이 필요함
            """.trimIndent()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFFE75480)) // 🔹 로딩 서클 추가
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                selectedImageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "선택된 이미지",
                        modifier = Modifier.size(300.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📅 생년월일", fontSize = 18.sp, color = Color.Gray)
                        Text(birthdate, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⏰ 태어난 시간", fontSize = 18.sp, color = Color.Gray)
                        Text(birthtime, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (!isAnalysisDone) {
                    Button(
                        onClick = { isLoading = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE75480)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("분석", fontSize = 24.sp, color = Color.White)
                    }
                }

                if (isAnalysisDone) {
                    Box(
                        modifier = Modifier
                            .height(300.dp) // 300dp 높이 제한
                            .verticalScroll(rememberScrollState()) // Box에 스크롤 적용
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()

                        ) {
                            Text(" 얼굴 분석 결과", fontSize = 28.sp)
                            Text(faceResult, fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(" 사주 분석 결과", fontSize = 28.sp)
                            Text(fortuneResult, fontSize = 24.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { navController.navigate("complete") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE75480)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("완료", fontSize = 24.sp, color = Color.White)
                }
            }
        }
    }
}
