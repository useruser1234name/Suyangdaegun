package com.ryh.suyangdaegun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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
                    android.util.Log.e("MainActivity", "Google 로그인 취소됨")
                }
            }

        setContent {
            AppNavigator(googleSignInLauncher, authManager)
        }
    }

    private fun navigateToAccession(uid: String) {
        val intent = android.content.Intent(this, AccessionActivity::class.java).apply {
            putExtra("uid", uid)
        }
        startActivity(intent)
        finish()
    }
}


@Composable
fun MainScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("메인 화면", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("matching") }) {
            Text("매칭 화면으로 이동")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { navController.navigate("chatList") }) {
            Text("채팅 리스트로 이동")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { navController.navigate("myPage") }) {
            Text("마이 페이지로 이동")
        }
    }
}


