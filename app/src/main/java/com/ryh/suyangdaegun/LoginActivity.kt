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
import com.google.firebase.auth.FirebaseAuth
import com.ryh.suyangdaegun.auth.AuthManager


class LoginActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authManager = (application as SuyangdaegunApp).authManager

        // 이미 로그인되어 있다면 메인으로 바로 이동
        if (FirebaseAuth.getInstance().currentUser != null) {
            navigateToMain()
            return
        }

        val googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                authManager.handleSignInResult(
                    data = result.data,
                    onSuccess = { isExistingUser, uid ->
                        // 로그인 성공 시 메인으로 이동
                        navigateToMain()
                    },
                    onFailure = { e -> Log.e("LoginActivity", "로그인 실패", e) }
                )
            } else {
                Log.e("LoginActivity", "Google 로그인 취소됨")
            }
        }

        setContent {
            AppNavigator(googleSignInLauncher = googleSignInLauncher, authManager = authManager)
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun LoginScreen(
    onGoogleSignInClick: () -> Unit,
    onKakaoSignInClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
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
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onKakaoSignInClick,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F8))
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_kakao),
                        contentDescription = "Kakao icon",
                        modifier = Modifier.size(30.dp).padding(end = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(80.dp))
                    Text("카카오로 로그인", fontSize = 20.sp, color = Color.Black)
                }
            }
            Spacer(modifier = Modifier.height(23.dp))
            Button(
                onClick = onGoogleSignInClick,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F8))
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google icon",
                        modifier = Modifier.size(30.dp).padding(end = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(80.dp))
                    Text("Google로 로그인", fontSize = 20.sp, color = Color.Black)
                }
            }
            Spacer(modifier = Modifier.height(23.dp))
            Button(
                onClick = { /* 네이버 로그인 (미구현) */ },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F8))
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_naver),
                        contentDescription = "Naver icon",
                        modifier = Modifier.size(30.dp).padding(end = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(80.dp))
                    Text("네이버로 로그인", fontSize = 20.sp, color = Color.Black)
                }
            }
        }
    }
}