package com.ryh.suyangdaegun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            AppNavigator(navController = navController)
        }
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

@Composable
fun AppNavigator(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("accession") { AccessionScreen(navController) }
        composable("main") { MainScreen(navController) }
        composable("matching") { MatchingScreen(navController) }
        composable("chatList") { ChatListScreen(navController) }
        composable("chatting") { ChattingScreen(navController) }
        composable("myPage") { MyPageScreen(navController) }
        composable("faceAnalysis") { FaceAnalysisScreen(navController) }
    }
}
