package com.ryh.suyangdaegun

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ryh.suyangdaegun.auth.AuthManager

@Composable
fun LoginScreen(navController: NavHostController, activity: Activity) {
    val authManager = AuthManager(activity)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "로그인 화면",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("gender") // 회원가입 네비게이션 경로
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "회원가입")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                authManager.signInWithGoogle(
                    onSuccess = { isExistingUser ->
                        if (isExistingUser) {
                            navController.navigate("main") // 메인 화면으로 이동
                        } else {
                            navController.navigate("gender") // 회원가입 화면으로 이동
                        }
                    },
                    onFailure = { exception ->
                        // 에러 처리
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Google 로그인")
        }
    }
}
