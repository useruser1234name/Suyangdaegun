package com.ryh.suyangdaegun

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Preview(showBackground = true)
@Composable
fun MatchingScreenPreview() {
    MatchingScreen(navController = rememberNavController())
}


@Composable
fun MatchingScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
    ) {

        Text(
            "홈",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 22.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Divider(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(290.dp)
                    .width(360.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_1),
                    contentDescription = "이미지 설명",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop  // 이미지 크기 조절 방식
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .height(290.dp)
                    .width(360.dp)
            ) {/*사진 불러오는 로직
            AsyncImage(
            model = "https://example.com/image.jpg",
            contentDescription = "이미지 설명",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )*/
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .height(290.dp)
                    .width(360.dp)
            ) {/*사진 불러오는 로직
            AsyncImage(
            model = "https://example.com/image.jpg",
            contentDescription = "이미지 설명",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )*/
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .height(290.dp)
                    .width(360.dp)
            ) {/*사진 불러오는 로직
            AsyncImage(
            model = "https://example.com/image.jpg",
            contentDescription = "이미지 설명",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )*/
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .height(290.dp)
                    .width(360.dp)
            ) {/*사진 불러오는 로직
            AsyncImage(
            model = "https://example.com/image.jpg",
            contentDescription = "이미지 설명",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )*/
            }

            Button(
                onClick = { navController.navigate("main") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2D3A31)
                )
            ) {
                Text("궁합이 딱 맞는 친구 더보기", fontSize = 28.sp)
            }
        }
    }
}