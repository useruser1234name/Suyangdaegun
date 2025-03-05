package com.ryh.suyangdaegun.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ryh.suyangdaegun.R
import com.ryh.suyangdaegun.model.ChatGptService
import com.ryh.suyangdaegun.model.UserHelper


//싹 다 더미
@Composable
fun MyPageScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "내 정보", fontSize = 25.sp,
            modifier = Modifier.height(50.dp)
        )

        Divider(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(20.dp))

        GridMenuScreen()

        Spacer(modifier = Modifier.height(20.dp))


        Text(
            text = "알림",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 16.dp),
            color = Color.Black
        )

        Divider(
            color = Color.Gray, // 선의 색상
            thickness = 0.5.dp,   // 선의 두께
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        NoticeList(
            noticeItems = listOf(
                "📢 공지사항",
                "🎉 이벤트",
                "고객센터",
                "📝 자주 묻는 질문",
                "1:1 문의하기"
            )
        )
        Divider(
            color = Color.Gray, // 선의 색상
            thickness = 0.5.dp,   // 선의 두께
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "We Contact",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 16.dp),
            color = Color.Black
        )

        Divider(
            color = Color.Gray, // 선의 색상
            thickness = 0.5.dp,   // 선의 두께
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 9.dp, horizontal = 16.dp)
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(45.dp)
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_instagram),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(45.dp)
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_discord),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(45.dp)
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_facebook),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


@Composable
fun NoticeList(noticeItems: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color.White)
            .clip(RoundedCornerShape(8.dp))
    ) {

        noticeItems.forEach { item ->
            ClickableMenuItem(text = item)


        }
    }
}

@Composable
fun ClickableMenuItem(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* 클릭 이벤트 처리 */ }
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Text(
            text = text,
            fontSize = 21.sp,
            modifier = Modifier.align(Alignment.CenterStart),
            color = Color.Black
        )
    }
}


@Composable
fun GridMenuScreen() {
    val menuItems = listOf(
        "오늘의 운세" to R.drawable.ic_todayunse,
        "정통 사주" to R.drawable.ic_jeontong,
        "로또 번호" to R.drawable.ic_lotto,
        "여행" to R.drawable.ic_travel,
        "공동구매" to R.drawable.ic_gonggu,
        "병원동행" to R.drawable.ic_hospital,
        "프리미엄" to R.drawable.ic_premium,
        "88다방" to R.drawable.ic_dabang,
        "모임" to R.drawable.ic_moim
    )

    var showDialog by remember { mutableStateOf(false) }
    var fortuneText by remember { mutableStateOf("운세를 불러오는 중...") }
    var isLoading by remember { mutableStateOf(false) }
    var isButtonDisabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "메뉴", fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Divider(
            color = Color.Gray,
            thickness = 0.5.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        // 🔹 isLoading, isButtonDisabled 전달 추가
        GridMenu(menuItems, isLoading, isButtonDisabled) { label ->
            if (label == "오늘의 운세") {
                isLoading = true
                isButtonDisabled = true

                UserHelper.getUserBirthInfo { birthdate, birthtime ->
                    if (birthdate.isNotEmpty() && birthtime.isNotEmpty()) {
                        ChatGptService().getTodayFortune(birthdate, birthtime) { result ->
                            fortuneText = result
                            isLoading = false
                            isButtonDisabled = false
                            showDialog = true
                        }
                    } else {
                        fortuneText = "⚠️ 생년월일 정보가 없습니다. 프로필을 업데이트하세요."
                        isLoading = false
                        isButtonDisabled = false
                        showDialog = true
                    }
                }
            }
        }

        if (showDialog) {
            FortuneDialog(fortuneText, isLoading) { showDialog = false }
        }
    }
}

@Composable
fun GridMenu(
    menuItems: List<Pair<String, Int>>,
    isLoading: Boolean,
    isButtonDisabled: Boolean,
    onItemClick: (String) -> Unit
) {
    Column {
        menuItems.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowItems.forEach { (label, imageRes) ->
                    MenuItem(label, imageRes, isLoading, isButtonDisabled, onItemClick)
                }
            }
        }
    }
}



@Composable
fun MenuItem(
    label: String,
    imageRes: Int,
    isLoading: Boolean,
    isButtonDisabled: Boolean,
    onItemClick: (String) -> Unit
) {
    val isTodayFortune = label == "오늘의 운세"

    Card(
        modifier = Modifier
            .size(100.dp)
            .padding(8.dp)
            .clickable(enabled = !isButtonDisabled) { onItemClick(label) }, // 🔹 버튼 비활성화 관리
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isTodayFortune && isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp)) // 🔹 버튼 내부 로딩
            } else {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = label,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Text(
                text = if (isTodayFortune && isLoading) "불러오는 중..." else label, // 🔹 로딩 중이면 텍스트 변경
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun FortuneDialog(fortuneText: String, isLoading: Boolean, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bbo), // 🍀 아이콘 추가
                    contentDescription = "운세 아이콘",
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF4CAF50) // 초록색 행운 느낌
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🌟 오늘의 운세",
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
                    .heightIn(min = 100.dp, max = 400.dp) // 다이얼로그 크기 조정
                    .verticalScroll(rememberScrollState()) // ✅ 스크롤 가능하도록 설정
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    Text(
                        text = "운세를 불러오는 중...",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                } else {
                    Text(
                        text = fortuneText,
                        fontSize = 18.sp,
                        lineHeight = 26.sp, // ✅ 줄 간격 늘려 가독성 향상
                        color = Color.DarkGray
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // 초록색 버튼
            ) {
                Text("닫기", fontSize = 18.sp, color = Color.White)
            }
        },
        shape = RoundedCornerShape(16.dp), // 모서리 둥글게
        containerColor = Color.White // 배경색
    )
}
