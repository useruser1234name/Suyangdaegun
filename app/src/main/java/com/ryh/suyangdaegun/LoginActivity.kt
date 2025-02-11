package com.ryh.suyangdaegun

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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class LoginActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authManager = (application as SuyangdaegunApp).authManager

        // 🔹 기존 로그인 세션 유지 여부 확인 (로그아웃 방지)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {  // ✅ Firebase에 등록되지 않은 경우 로그인 화면 유지
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
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 앱 로고 (R.drawable.suyang를 실제 로고 리소스로 대체)
        Image(
            painter = painterResource(id = R.drawable.suyang),
            contentDescription = "App Logo",
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onGoogleSignInClick,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Google 로그인", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onKakaoSignInClick,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Kakao 로그인", fontSize = 18.sp)
        }
    }
}
