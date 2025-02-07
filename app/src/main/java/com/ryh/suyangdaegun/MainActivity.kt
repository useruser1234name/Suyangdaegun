package com.ryh.suyangdaegun

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ryh.suyangdaegun.auth.AuthManager

class MainActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Application에서 authManager 가져오기
        authManager = (application as SuyangdaegunApp).authManager

        val googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    authManager.handleSignInResult(
                        data = result.data,
                        onSuccess = { isExistingUser, uid ->
                            if (isExistingUser) {
                                finish()  // ✅ Main 화면이므로 그대로 유지
                            } else {
                                navigateToAccession(uid)
                            }
                        },
                        onFailure = { e -> android.util.Log.e("MainActivity", "로그인 실패", e) }
                    )
                } else {
                    android.util.Log.e("MainActivity", "Google 로그인이 취소됨")
                }
            }

        setContent {
            AppNavigator(googleSignInLauncher, authManager)
        }
    }

    private fun navigateToAccession(uid: String?) {
        if (uid.isNullOrEmpty()) {
            Log.e("MainActivity", "회원가입 화면으로 이동하려 했으나 UID가 없음")
            return
        }

        val intent = Intent(this, AccessionActivity::class.java).apply {
            putExtra("uid", uid)
        }
        startActivity(intent)
        finish()
    }

}


@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MainScreen(navController = rememberNavController())
}

@Composable
fun MainScreen(navController: NavHostController) {
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


