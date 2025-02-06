package com.ryh.suyangdaegun

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryh.suyangdaegun.auth.AuthManager

class LoginActivity : ComponentActivity() {
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
                                navigateToMain()
                            } else {
                                navigateToAccession(uid)
                            }
                        },
                        onFailure = { e -> Log.e("LoginActivity", "로그인 실패", e) }
                    )
                } else {
                    Log.e("LoginActivity", "Google 로그인 취소됨")
                }
            }

        setContent {
            AppNavigator(googleSignInLauncher, authManager)
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToAccession(uid: String) {
        val intent = Intent(this, AccessionActivity::class.java).apply {
            putExtra("uid", uid)
        }
        startActivity(intent)
        finish()
    }
}

@Composable
fun LoginScreen(onGoogleSignInClick: () -> Unit,
                onKakaoSignInClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.suyang),
                contentDescription = "Logo Image",
                modifier = Modifier.size(width = 230.dp, height = 50.dp),
                contentScale = ContentScale.Inside
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "새로운 인연", fontSize = 36.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(230.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ,
//                .fillMaxHeight(0.4f)
//            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(
                onClick = onKakaoSignInClick, // Kakao 로그인 -> 회원가입 화면
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                ,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5F5F8)
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_kakao),
                        contentDescription = "Kakao icon",
                        modifier = Modifier
                            .size(30.dp)
                            .padding(end = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(80.dp))
                    Text("카카오로 로그인", color = Color.Black, fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(23.dp))

            Button(onClick = onGoogleSignInClick,
                modifier = Modifier
                    .fillMaxWidth() .height(60.dp)
                ,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5F5F8)
                )
            )
            {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "google icon",
                        modifier = Modifier
                            .size(30.dp)
                            .padding(end = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(80.dp))
                    Text("Google로 로그인", color = Color.Black, fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(23.dp))
            Button(
                onClick = { }, // Naver 로그인 -> 회원가입 화면
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                ,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5F5F8)
                )
            )
            {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_naver),
                        contentDescription = "Kakao icon",
                        modifier = Modifier
                            .size(30.dp)
                            .padding(end = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(80.dp))

                    Text("네이버로 로그인", color = Color.Black, fontSize = 20.sp)
                }
            }
        }
    }
}