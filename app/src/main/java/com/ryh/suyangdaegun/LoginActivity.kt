package com.ryh.suyangdaegun

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ryh.suyangdaegun.auth.AuthManager

@Composable
fun LoginScreen(navController: NavHostController, activity: Activity) {
    val authManager = remember { AuthManager(activity) }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("로그인 화면", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            authManager.signInWithGoogle(
                onSuccess = { isExistingUser ->
                    if (isExistingUser) {
                        navController.navigate("main") // 기존 회원이면 바로 메인 화면 이동
                    } else {
                        navController.navigate("gender") // 신규 회원이면 회원가입 진행
                    }
                },
                onFailure = { e -> Log.e("LoginScreen", "로그인 실패", e) }
            )
        }) {
            Text("Google 로그인")
        }
    }
}