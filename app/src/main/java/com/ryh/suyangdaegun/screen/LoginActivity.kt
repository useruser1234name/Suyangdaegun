package com.ryh.suyangdaegun.screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.ryh.suyangdaegun.auth.AuthManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.ryh.suyangdaegun.R
import com.ryh.suyangdaegun.model.SuyangdaegunApp

class LoginActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authManager = (application as SuyangdaegunApp).authManager

        //사실 login screen 전에 loading screen을 적용하여 인증을 받으면
        // 앱 삭제 안 하고도 인증을 받아 로그인 화면 으로 바로 이동이 가능하지만 이 역시 귀찮음
        //근데 실험한다고 계속 설치하는게 더 귀찮음 근데 귀찮음
        // 🔹 기존 로그인 세션 유지 여부 확인 (로그아웃 방지)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {  // Firebase에 등록되지 않은 경우 로그인 화면 유지
            Log.d("LoginActivity", "No user logged in – staying in LoginActivity")
        } else {
            Log.d("LoginActivity", "Already logged in – navigating to MainActivity")
            navigateToMain()
        }


        // 🔹 Google 로그인 액티비티 실행
        val googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    authManager.handleSignInResult(
                        data = result.data,
                        onSuccess = { isNewUser, uid, email ->
                            Log.d("LoginActivity", "Login success: uid=$uid, isNewUser=$isNewUser")
                            if (isNewUser) {
                                navigateToAccession(uid)
                            } else {
                                navigateToMain()
                            }
                        },
                        onFailure = { e ->
                            Log.e("LoginActivity", "Login failed", e)
                            Toast.makeText(this, "로그인 실패: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                } else {
                    Log.e("LoginActivity", "Google login cancelled")
                    Toast.makeText(this, "로그인이 취소되었습니다.", Toast.LENGTH_LONG).show()
                }
            }

        setContent {
            LoginScreen(
                onGoogleSignInClick = {
                    authManager.signInWithGoogle(
                        googleSignInLauncher,
                        onFailure = { e ->
                            Log.e("LoginActivity", "Google 로그인 실패", e)
                            Toast.makeText(this, "로그인 실패: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                },
                onKakaoSignInClick = { /* Kakao 로그인 추가 */ }
            )
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun navigateToAccession(uid: String) {
        startActivity(Intent(this, AccessionActivity::class.java).apply {
            putExtra("uid", uid)
        })
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
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(100.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_sodamyeon_removebg_preview),
                contentDescription = "Logo Image",  //누끼 따서 넣었는데 흐릿한게 나 같음
                modifier = Modifier.size(width = 400.dp, height = 200.dp),
                contentScale = ContentScale.Inside
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "새로운 인연", fontSize = 36.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = onGoogleSignInClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F8))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(28.dp),

                    )
                Text(
                    text = "Google 로그인",
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onKakaoSignInClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F8))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically, // 수직 정렬
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_kakao),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(30.dp)
                )

                Text(
                    text = "Kakao 로그인",
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onKakaoSignInClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F8))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically, // 수직 정렬
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_naver),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(30.dp)
                )

                Text(
                    text = "Naver 로그인",
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
