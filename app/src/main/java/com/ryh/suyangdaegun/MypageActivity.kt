package com.ryh.suyangdaegun

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            "메뉴", fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )

        Divider(
            color = Color.Gray, // 선의 색상
            thickness = 0.5.dp,   // 선의 두께
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        GridMenu(menuItems)
    }
}

@Composable
fun GridMenu(menuItems: List<Pair<String, Int>>) {
    Column {
        menuItems.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowItems.forEach { (label, imageRes) ->
                    MenuItem(label, imageRes)
                }
            }
        }
    }
}

@Composable
fun MenuItem(label: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .size(100.dp)
            .padding(8.dp)
            .clickable { /* 클릭 이벤트 추가 가능 */ },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = label,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Text(label, fontSize = 18.sp, color = Color.Black)
        }
    }
}
