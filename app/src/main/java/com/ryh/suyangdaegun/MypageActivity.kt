package com.ryh.suyangdaegun

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MyPageScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("마이 페이지 화면", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("main") }) {
            Text("메인 화면으로 돌아가기")
        }
    }
}

@Composable
fun LogoutButton(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()

    Button(onClick = {
        auth.signOut()
        navController.navigate("login") // 로그아웃 후 로그인 화면으로 이동
    }) {
        Text("로그아웃")
    }
}
